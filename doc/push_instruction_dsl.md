# Push Instruction DSL

The Push Instruction DSL is a highly-constrained domain-specific language for defining Push instructions.

An Instruction operates on an Interpreter by sending it (thread-first) through the steps of the DSL script, producing a changed Interpreter at each step, and (optionally) recording and manipulating ephemeral scratch variables as it does so. These scratch variables are always local to the DSL interpreter environment, and are discarded as soon as the Instruction code ends.

## DSL fundamentals

For several practical reasons the Push Instruction DSL is simple but expressive, and will feel highly constrained compared to writing "freehand" Clojure code. While the definitions of some instructions may require more characters than you might normally type in an _ad hoc_ project, by using the DSL the `Interpreter` is able to:

- automatically infer the stack types and minimum number of items that must appear on those stacks
- simplify and optimize coded definitions _in the background_
- automatically generate unit tests for edge cases (_e.g._, missing arguments)
- monitor and summarize interactions and similarities between registered Instructions _from their source code_, simplifying the design, implementation, and management of new types, instructions and experiments
- better support validation of complex Instruction types
- change behavior in response to "missing instructions" or "redefined instructions" automatically
- change Interpreter behavior globally without changing the code of each Instruction (for example, one can easily explore the behavior of programs when arguments are _not deleted_ by calling Instructions)
- maintainability of the Push interpreter codebase
- simplified logging and debugging information through globally-controlled side-effects

As I said, there are some constraints:

- only one item can be popped from a stack in any given step
- if an item is to be referred to in a later step, it _must_ be stored in a named scratch variable
- arbitrary Clojure functions can be invoked, but it can _only_ use named scratch variables or inline literals as arguments
- the general state of the `Interpreter` is not available; only stack items and a few other state variables can be read; only stacks can be written

## A couple of (working) examples

Note: these are calls to the `build-instruction` macro, which creates a new `Instruction` record with various other settings besides the transaction itself. The last few lines of each (after the `:tags` line) are the transaction proper.

`:integer-lt` (less than):

~~~clojure
(def integer-lt
  (core/build-instruction
    integer-lt
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg2 :arg1] #(< %1 %2) :as :less?)
    (push-onto :boolean :less?)))
~~~

`:code-do*range`:

~~~clojure
(def code-do*range 
  (core/build-instruction
    code-do*range
    :tags #{:code :base :iterator}
    (consume-top-of :code :as :to-do)
    (consume-top-of :integer :as :current-index)
    (consume-top-of :integer :as :destination-index)
    (calculate [:destination-index :current-index]
               #(compare %1 %2)
               :as :direction)
    (calculate [:current-index :direction]
               #(+ %1 %2) 
               :as :new-index)
    (calculate [:direction :new-index :destination-index :to-do]
                #(if (zero? %1)
                  (list)
                  (list %2 %3 :code_quote %4 :code_do*range))
                :as :continuation)
    (put-onto :exec :continuation)))
~~~

## DSL scratch variables

All scratch variables are referred to by Clojure keywords (not symbols). These are keys of a transient local store, and you shouldn't have to worry about namespace leakage. However they should be unique; saving a new value to an already-defined keyword key will overwrite the old value.

## DSL transactions

When a Push `Instruction` is invoked, it receives the `Interpreter` state as its (only) argument. You can visualize this as though the `Interpreter` is passed through each step of the defined _transaction_, and is transformed along the way.

So for example, the `integer_add` instruction can be defined by this _transaction_:

```clojure
(
  (consume-top-of :integer :as :int1)
  (consume-top-of :integer :as :int2)
  (calculate [:int1 :int2] #(+ %1 %2) :as :sum)
  (put-onto :integer :sum)
)
```

When this transaction is turned into a _function_, the actual Clojure is something more like this:

```clojure
(fn [interpreter]
  ( -> [interpreter {}]
       (consume-top-of , :integer :as :int1)
       (consume-top-of , :integer :as :int2)
       (calculate , [:int1 :int2] #(+ %1 %2) :as :sum)
       (put-onto , :integer :sum)
       (first , )))
```

I've used the comma (whitespace in Clojure) to indicate the places in each line where the threaded argument is invoked. In other words, what's threaded through the instructions (using Clojure's `thread-first` macro, `->`) is a vector composed of the interpreter and the `scratch` hashmap, where local state is stored in the process of running the code.

Note that none of the `scratch` information exists at the start of the transaction, and it is all deleted at the end. Only the `Interpreter` state can be changed.

## DSL instructions

- [X] `calculate [args fn :as local]`
  
  Example: `(calculate [:int1 :int2] #(+ %1 %2) :as :sum)`

  `args` is a vector of keywords, referring to scratch variables

  `fn` is an arbitrary Clojure inline function which _must_ refer to its arguments positionally as listed in the `args` vector; the function can of course invoke Clojure symbols defined outside the instruction, and can contain literals, but should not refer to any _arguments_ other than those listed in that vector.

  The result of invoking `fn` on `args` is saved into scratch variable `local`, overwriting any previous value.

- [X] `consume-nth-of [stackname :at where :as local]`

  Example: `consume-nth-of :boolean :at 3 :as :bool3]`

  Example: `consume-nth-of :boolean :at :middle :as :bool3]`
  
  Removes the item in position `where` from the named stack, and stores it in scratch variable `local`.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

  The relative order of `:at` and `:as` arguments are not crucial, except for readability, but both must be present.
    
- [X] `consume-stack [stackname :as local]`

  Example: `consume-stack :code as :old-code`

  Saves the entire named stack into `local` and clears it in the `Interpreter`.

- [X] `consume-top-of [stackname :as local]`

  Example: `consume-top-of :float as :numerator`
  
  Pops an item from `stackname` and stores under key `local`. Raises an Exception if `stackname` is empty or undefined. Will overwrite any prior value stored in `local`.

- [X] `count-of [stackname :as local]`

  Example: `count-of :float :as :float-size`

  Stores the number of items in stack `stackname` in scratch variable `local`.

- [X] `delete-nth-of [stackname :at where]`
  
  Example: `delete-nth-of :integer :at -19`

  Example: `delete-nth-of :integer :at :bad-number`

  Removes the item in position `where` from the named stack without storing it.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

- [X] `delete-top-of [stackname]`
  
  Example: `delete-top-of :float`

  Pop an item (and discards it) from `stackname`.

  Raises an Exception if `stackname` is empty or undefined.

- [X] `delete-stack [stackname]`

  Example: `delete-stack :vector_of_boolean`

  Empty the named stack in the `Interpreter`.

- [X] `insert-as-nth-of [stackname :local :at where]`
  
  Example: `insert-as-nth-of :integer :new-int :at -19`

  Example: `insert-as-nth-of :boolean :new-bool :at :index`

  Inserts the indicated item from `scratch` so that it is now in position `where` in the named stack.

  If `where` is an integer literal, then the index of the item inserted is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking that up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

- [X] `push-onto [stackname local]`

  Example: `push-onto :integer :sum`

  Push the scratch value `local` onto the top of the indicated stack. If the `local` is `nil`, that's OK; nothing bad will happen.

- [X] `push-these-onto [stackname [locals]]`

  Example: `push-these-onto :integer [:quotient :remainder]`

  Push _each_ of the indicated scratch values in the _vector_ onto the top of the indicated stack, one at a time, first one pushed first, last one pushed last (and thus ending up at the top). If any of them is `nil` that's OK; nothing bad will happen.

- [ ] `save-counter [:as local]`
  
  Example: `save-counter :as :steps`

  Saves the current interpreter counter value in `local`.

- [ ] `save-input-names [:as local]`

  Example: `save-input-names :as :all-variables`

  Saves a list of all registered inputs pseudo-instructions (the keywords used to refer to them in code) in scratch variable `local`. Order of the list should be assumed to be arbitrary.

- [ ] `save-instructions [:as local]`

  Example: `save-instructions :as :registered`

  Saves a list of the keywords used to refer to all registered `Instructions` in the running `Interpreter` in scratch variable `local`. Order of the list should be assumed to be arbitrary.

- [X] `save-nth-of [stackname :at where :as local]`
  
  Example: `save-nth-of :boolean :at 7 :as :seventh`

  Example: `save-nth-of :boolean :at :best-one :as :best-val`

  Copies the item in position `where` into the scratch variable `local`.

  If `where` is an integer literal, then the index of the item removed is `(mod where (count stackname))`. If `where` is a keyword, then the index is obtained by looking it up in the scratch storage.

  If the index (obtained via a local scratch value) is not an integer (including `nil`), an Exception is raised. If it is an integer, it is treated as before, and `mod`ded into the appropriate range.

  The relative order of `:at` and `:as` arguments are not crucial, except for readability, but both must be present.

- [X] `save-top-of [stackname :as local]`

  Example: `save-top-of :exec :as :next-item`

  Copies the top item from `stackname` into scratch variable `local` without removing it from the stack.

- [X] `save-stack [stackname :as local]`

  Example: `save-stack :float :as :unsorted`

  Saves a copy of the entire list `stackname` into scratch variable `local`. Does not clear the stack.

- [X] `replace-stack [stackname local]`
  
  Example: `replace-stack :integer :converted-floats`

  Replace the indicated stack with the contents of the scratch variable `local`.

  If the stored value is `nil`, empty the stack; if it is a list, _replace_ the stack with that list; if it is not a list, replace the stack with a new list _containing only that one value_. For example, if the stored value is `'(1 2 3)` then the new stack will be `'(1 2 3)`; if the stored value is `[1 2 3[`, the new stack will be `'([1 2 3])`.


### Possible future extensions

- `(consume-all-of % :integer :foo :boolean :bar :integer :baz :float :qux)`
- `(place-all-of % :integer :foo :boolean :bar :integer :baz :float :qux)`
- ?