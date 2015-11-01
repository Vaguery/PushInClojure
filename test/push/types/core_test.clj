(ns push.types.core-test
  (:use midje.sweet)
  (:use [push.types.core])
  (:require [push.interpreter.interpreter-core :as i])
)


;;;; type information

;; PushType records


(fact "`make-type` takes a keyword and recognizer"
  (make-type :integer :recognizer integer?) =>
    {:stackname :integer, :recognizer integer?, :attributes #{}})


(fact "`make-type` takes an optional :attributes set"
  (:attributes (make-type 
                  :integer 
                  :recognizer integer? 
                  :attributes #{:comparable :numeric})) =>
  #{:comparable :numeric})


;; :visible types


(fact "`make-visible` takes adds the :visible attribute to a PushType record"
  (:attributes (make-visible (make-type :integer))) => #{:visible})



(fact "stackdepth-instruction returns an Instruction with the correct stuff"
  (let [foo-depth (stackdepth-instruction (make-type :foo))]
  (class foo-depth) => push.instructions.instructions_core.Instruction
  (:needs foo-depth) => {:foo 0, :integer 0}
  (:token foo-depth) => :foo-stackdepth
  (i/get-stack
    (i/execute-instruction
      (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-depth)
      :foo-stackdepth)
    :integer) => '(2)
  ))


(fact "empty?-instruction returns an Instruction with the correct stuff"
  (let [foo-none? (empty?-instruction (make-type :foo))]
  (class foo-none?) => push.instructions.instructions_core.Instruction
  (:needs foo-none?) => {:boolean 0, :foo 0}
  (:token foo-none?) => :foo-empty?
  (i/get-stack
    (i/execute-instruction
      (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-none?)
      :foo-empty?)
    :boolean) => '(false)
  ))



; (fact "`make-visible` takes adds an x-stackdepth instruction to a PushType record"
;   (keys (:instructions (make-visible (make-type :integer)))) =>
;     '(:integer-stackdepth)
;   )