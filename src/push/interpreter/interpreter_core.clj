(ns push.interpreter.interpreter-core)


(defrecord Interpreter [program stacks instructions counter])


(def core-stacks
  "the basic types central to the Push language"
    {:boolean '()
    :char '()
    :code '()
    :input '()
    :integer '() 
    :exec '()
    :float '()
    :string '()
    })


(defn make-interpreter
  "creates a new Interpreter record
  With no arguments, it has an empty :program, the :stacks include core types and are empty, no :instructions are registered, and the counter is 0.

  Any of these can be specified by key."
  [& {:keys [program stacks instructions counter]
      :or {program []
           stacks core-stacks
           instructions {}
           counter 0}}]
  (->Interpreter program (merge core-stacks stacks) instructions counter))



(defn register-instruction
  "Takes an Interpreter and an Instruction, and attempts to add the Instruction to the :instructions map of the Interpreter, keyed by its :token."
  [interpreter instruction]
  (let [token (:token instruction)
        registry (:instructions interpreter)]
    (if (contains? registry token)
      (throw (Exception. (str "Push Instruction Redefined:'" token "'")))
      (assoc-in interpreter [:instructions (:token instruction)] instruction))))


(defn contains-at-least?
  "Takes an interpreter, a stack name, and a count; returns true if the named stack exists, and has at least that many items on it"
  [interpreter stack limit]
  (let [that-stack (get-in interpreter [:stacks stack])]
    (and 
      (<= limit (count that-stack))
      (some? that-stack))))


(defn ready-for-instruction?
  "Takes an Interpreter (with registered instructions) and a keyword instruction token, and returns true if the number of items on the stacks meets or exceeds all the specified :needs for that instruction. Returns false if the instruction is not registered."
  [interpreter token]
  (let [needs (get-in interpreter [:instructions token :needs])
        recognized (contains? (:instructions interpreter) token)]
    (and
      recognized
      (reduce-kv
        (fn [truth k v] (and truth (contains-at-least? interpreter k v)))
        true
        needs))))


(defn execute-instruction
  "Takes an Interpreter and a token, and executes the registered Instruction using the Interpreter as the (only) argument. Raises an exception if the token is not registered."
  [interpreter token]
  (let [unrecognized (not (contains? (:instructions interpreter) token))
        ready (ready-for-instruction? interpreter token)]
  (cond
    unrecognized (throw (Exception. (str "Unknown Push instruction:'" token "'")))
    ready  ((:transaction (get-in interpreter [:instructions token])) interpreter)
    :else interpreter)))


(defn push-item
  "Takes an Interpreter, a stack name and a Clojure expression, and returns the Interpreter with the item pushed onto the specified stack. If the stack doesn't already exist, it is created."
  [interpreter stack item]
  (let [old-stack (get-in interpreter [:stacks stack])]
    (assoc-in interpreter [:stacks stack] (conj old-stack item))))


(defn load-items
  "Takes an Interpreter, a stack name, and a seq, and conj'es each item onto the named stack of the interpreter (using Clojure's `into` transducer). Used primarily for loading code onto the :exec stack."
  [interpreter stack item-list]
  (let [old-stack (get-in interpreter [:stacks stack])]
    (assoc-in interpreter [:stacks stack] (into old-stack (reverse item-list)))))


(defn get-stack
  "A convenience function which returns the named stack from the interpreter"
  [interpreter stack]
  (get-in interpreter [:stacks stack]))


(defn set-stack
  "A convenience function which replaces the named stack with the indicated list"
  [interpreter stack new-value]
  (assoc-in interpreter [:stacks stack] new-value))


(defn clear-stack
  "Empties the named stack."
  [interpreter stack]
  (assoc-in interpreter [:stacks stack] (list)))


(defn boolean?
  "a checker that returns true if the argument is the literal `true` or the literal `false`"
  [item]
  (or (false? item) (true? item)))


(defn instruction?
  "takes an Interpreter and a keyword, and returns true if the keyword is a key of the :instructions registry in that Interpreter instance"
  [interpreter token]
  (contains? (:instructions interpreter) token))


(defn handle-item
  "Takes an Interpreter and an item, and either recognizes and invokes a keyword registered in that Interpreter as an instruction, or sends the item to the correct stack (if it exists). Throws an exception if the Clojure expression is not recognized explicitly as a registered instruction or some other kind of Push literal."
  [interpreter item]
  (cond
    (instruction? interpreter item) (execute-instruction interpreter item)
    (integer? item) (push-item interpreter :integer item)
    (boolean? item) (push-item interpreter :boolean item)
    (char? item) (push-item interpreter :char item)
    (float? item) (push-item interpreter :float item)
    (string? item) (push-item interpreter :string item)
    (list? item) (load-items interpreter :exec item)
    :else (throw
      (Exception. (str "Push Parsing Error: Cannot interpret '" item "' as a Push item.")))))


(defn clear-all-stacks
  "removes all items from all stacks in an Interpreter"
  [interpreter]
  (let [stacklist (keys (:stacks interpreter))]
    (assoc interpreter :stacks (reduce #(assoc %1 %2 '()) {} stacklist))))



(defn reset-interpreter
  "takes an Interpreter instance and:
  - sets the counter to 0
  - clears all non-:exec stacks
  - puts the program onto the :exec stack"
  [interpreter]
    (-> interpreter
        (clear-all-stacks)
        (assoc , :counter 0)
        (load-items :exec (:program interpreter)))
  )