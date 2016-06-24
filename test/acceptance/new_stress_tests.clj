(ns acceptance.new-stress-tests
  (:require [push.core :as push]
            [push.type.definitions.quoted :as qc]
            [com.climate.claypoole :as cp]
            )
  (:use midje.sweet))


;; literal generators


(defn some-long
  [i]
  (-' (rand-int i) (/ i 2)))


(defn some-double
  [i]
  (-' (rand i) (/ i 2)))


(defn some-rational
  [i]
  (/ (some-long i) (inc (rand-int i))))


(defn some-bigint
  [i]
  (bigint (some-long i)))


(defn some-bigdec
  [i]
  (bigdec (some-double i)))


(defn some-ascii
  [i]
  (char (+ 33 (rand-int (- 126 33)))))


(defn some-string
  [i]
  (clojure.string/join (take i (repeatedly #(char (+ 33 (rand-int (- 126 33))))))))


(defn vector-of-stuff
  [f i j]
  (into [] (take j (repeatedly #(f i)))))


(def my-interpreter
  (push/interpreter))


(defn some-instruction
  []
  (rand-nth (push/known-instructions my-interpreter)))


(declare some-codeblock)


(def all-the-letters (map keyword (map str (map char (range 97 123)))))


(defn some-item
  [i]
  (if (< 3/5 (rand))
    (rand-nth [
      (some-long 10)
      (some-long 100)
      (some-long 100000)
      (some-double 10)
      (some-double 100)
      (some-double 100000)
      (some-rational 100000)
      (some-bigint 10000000)
      (some-bigdec 10000000)
      (some-ascii 1)
      (some-string 10)
      (vector-of-stuff some-long 100 i)
      (vector-of-stuff some-double 100 i)
      (rand-nth all-the-letters)
      (some-codeblock (dec i))
      (qc/push-quote(some-codeblock 20))
      ])
    (some-instruction)))


(defn some-codeblock
  [i]
  (take i (repeatedly #(some-item i))))


(defn some-program
  [i j]
  (into [] (take i (repeatedly #(some-item j)))))


(defn some-bindings
  [i]
  (zipmap
      (take i all-the-letters)
      (repeatedly #(some-item 10))
      ))

;;;;

(defn run-program
  [program bindings]
  (push/run my-interpreter program 3000 :bindings bindings))


(defn interpreter-details
  [i]
  {:steps (:counter i)
   :errors (count (push/get-stack i :error))
   :items (frequencies (map :item (push/get-stack i :error)))
   :scalar (push/get-stack i :scalar)
  })


(def many-programs
  (take 100 (repeatedly #(some-program 100 10))))


(def sample-bindings
  (some-bindings 10))



(defn launch-some-workers
  []
  (do 
    (doall
      (cp/pmap 32
        #(time
          (println
            (str "\n\n"
              (interpreter-details
                (run-program % sample-bindings)))))
        many-programs))
    (println :done)))


(fact :danger "run some workers in parallel"
  (launch-some-workers) =not=> (throws))