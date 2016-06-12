(ns push.instructions.standard.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.tagspace])
  (:use [push.instructions.aspects])
  )




(tabular
  (fact ":tagspace-new pushes a new tagspace"
    (register-type-and-check-instruction
        ?set-stack ?items tagspace-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction    ?get-stack     ?expected
    :tagspace   '()         :tagspace-new    :tagspace    (list (make-tagspace)))


(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-count` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy)}    :tagspace-count           {:exec  (list (list 3 taggy))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-count           {:exec  (list (list 0 (make-tagspace)))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))


(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-keys` pushes the keys and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy)}    :tagspace-keys           {:exec  (list (list '(-4 2 6) taggy))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-keys           {:exec  (list (list '() (make-tagspace)))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-values` pushes the values and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy)}    :tagspace-values           {:exec  (list (list '(3 1 5) taggy))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-values           {:exec  (list (list '() (make-tagspace)))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupint` pops an :integer and a :tagspace and retrieves the indicated item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :integer '(3)
     :tagspace (list taggy)}    :tagspace-lookupint      {:exec     '(4)
                                                          :integer  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :integer '(33)
     :tagspace (list taggy)}    :tagspace-lookupint      {:exec     '(2)
                                                          :integer  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :integer '(33)
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupint      {:exec     '()
                                                          :integer  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))


(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupfloat` pops an :float and a :tagspace and retrieves the indicated item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :float '(2.2)
     :tagspace (list taggy)}    :tagspace-lookupfloat    {:exec    '(4)
                                                          :float   '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :float '(33.0)
     :tagspace (list taggy)}    :tagspace-lookupfloat    {:exec    '(2)
                                                          :float   '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :float '(-33.0)
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupfloat    {:exec    '()
                                                          :float   '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupintegers` pops an :integers and a :tagspace and retrieves ALL the indicated items"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :integers '([-3 0 3 6 9])
     :tagspace (list taggy)}    :tagspace-lookupintegers  {:exec     '((2 2 4 2 2))
                                                          :integers  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :integers '([])
     :tagspace (list taggy)}    :tagspace-lookupintegers      {:exec     '(())
                                                          :integers  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :integers '([1 8])
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupintegers      {:exec     '(())
                                                          :integers  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupfloats` pops a :floats and a :tagspace and retrieves ALL the indicated items"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :floats '([0.1 1.2 2.3 3.4 4.5])
     :tagspace (list taggy)}    :tagspace-lookupfloats  {:exec     '((2 4 4 6 6))
                                                          :floats  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :floats '([])
     :tagspace (list taggy)}    :tagspace-lookupfloats      {:exec     '(())
                                                          :floats  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :floats '([8.1 9.2])
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupfloats      {:exec     '(())
                                                          :floats  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupvector` pops an :vector and a :tagspace and retrieves ALL the indicated items"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :vector '([-3.2 :foo 3 6 9])
     :tagspace (list taggy)}    :tagspace-lookupvector  {:exec     '((2 :foo 4 2 2))
                                                          :vector  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([-3 :foo \g :integer-add [6.2] 11/8])
     :tagspace (list taggy)}    :tagspace-lookupvector   {:exec 
                                                            '((2 :foo \g :integer-add [6.2] 4))
                                                          :vector  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([1 8])
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupvector   {:exec     '(())
                                                          :vector  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace)]

(tabular
  (fact "`:foo-tagwithinteger` pops an :integer and a :foo and stores the latter in a :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '("bar")
     :integer '(71)
     :tagspace (list taggy)}    :foo-tagwithinteger      {:foo      '()
                                                          :integer  '()
                                                          :tagspace (list (assoc-in taggy [:contents 71] "bar"))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()
     :integer '(71)
     :tagspace (list taggy)}    :foo-tagwithinteger      {:foo      '()
                                                          :integer  '(71)
                                                          :tagspace (list taggy)}
    ))



(let 
  [taggy1  (make-tagspace {1 2 3 4 5 6})
   taggy2  (make-tagspace {1 -2 3 -4 5.5 6.6})]

(tabular
  (fact "`:tagspace-merge` merges two tagspaces"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy1 taggy2)}
                               :tagspace-merge          {:tagspace (list
                                                          (make-tagspace {5.5 6.6, 1 2, 3 4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy2 taggy1)}
                               :tagspace-merge          {:tagspace (list
                                                          (make-tagspace {5.5 6.6, 1 -2, 3 -4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace)]

(tabular
  (fact "`:foo-tagwithfloat` pops an :integfloater and a :foo and stores the latter in a :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '("bar")
     :float '(71.71)
     :tagspace (list taggy)}    :foo-tagwithfloat      {:foo      '()
                                                          :float  '()
                                                          :tagspace (list (assoc-in taggy [:contents 71.71] "bar"))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()
     :float '(71.71)
     :tagspace (list taggy)}    :foo-tagwithfloat      {:foo      '()
                                                          :float  '(71.71)
                                                          :tagspace (list taggy)}
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-offsetfloat` pops a :float and a :tagspace and moves the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:float '(0.5)
     :tagspace (list taggy)}    :tagspace-offsetfloat   {:float  '()
                                                        :tagspace (list
                                                          (make-tagspace {1.5 2, 3.5 4, 5.5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-11.5)
     :tagspace (list taggy)}    :tagspace-offsetfloat   {:float  '()
                                                        :tagspace (list
                                                        (make-tagspace {-10.5 2, -8.5 4, -6.5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-0.0)
     :tagspace (list taggy)}    :tagspace-offsetfloat   {:float  '()
                                                        :tagspace (list
                                                        (make-tagspace {1.0 2 3.0 4 5.0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))


(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-max` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy)}    :tagspace-max           {:exec  (list (list 6 taggy))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-max           {:exec  (list (make-tagspace))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-min` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy)}    :tagspace-min           {:exec  (list (list -4 taggy))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-min           {:exec  (list (make-tagspace))
                                                        :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-offsetint` pops a :float and a :tagspace and moves the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:integer '(17)
     :tagspace (list taggy)}    :tagspace-offsetint   {:integer  '()
                                                        :tagspace (list
                                                          (make-tagspace {18 2, 20 4, 22 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(-121)
     :tagspace (list taggy)}    :tagspace-offsetint   {:integer  '()
                                                        :tagspace (list
                                                        (make-tagspace {-120 2, -118 4, -116 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(0)
     :tagspace (list taggy)}    :tagspace-offsetint   {:integer  '()
                                                        :tagspace (list
                                                        (make-tagspace {1 2 3 4 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-scaleint` pops an :integer and a :tagspace and scales the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:integer '(3)
     :tagspace (list taggy)}    :tagspace-scaleint       {:integer  '()
                                                          :tagspace (list
                                                            (make-tagspace {3 2, 9 4, 15 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(-3)
     :tagspace (list taggy)}    :tagspace-scaleint       {:integer  '()
                                                          :tagspace (list
                                                            (make-tagspace {-3 2, -9 4, -15 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(0)
     :tagspace (list taggy)}    :tagspace-scaleint       {:integer  '()
                                                          :tagspace (list
                                                            (make-tagspace {0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-scalefloat` pops a :float and a :tagspace and scales the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:float '(0.5)
     :tagspace (list taggy)}    :tagspace-scalefloat   {:float  '()
                                                        :tagspace (list
                                                          (make-tagspace {0.5 2, 1.5 4, 2.5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-1.5)
     :tagspace (list taggy)}    :tagspace-scalefloat   {:float  '()
                                                        :tagspace (list
                                                        (make-tagspace {-7.5 6, -4.5 4, -1.5 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-0.0)
     :tagspace (list taggy)}    :tagspace-scalefloat   {:float  '()
                                                        :tagspace (list
                                                        (make-tagspace {0.0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-0.0)
     :tagspace (list (make-tagspace))}    :tagspace-scalefloat   {:float  '()
                                                        :tagspace (list
                                                        (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-splitwithint` pops an :integer and a :tagspace and produces two new tagspaces (pushed to :exec)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:integer '(3)
     :tagspace (list taggy)}  :tagspace-splitwithint     {:integer  '()
                                                          :exec (list (list
                                                            (make-tagspace {1 2})
                                                            (make-tagspace {3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(4)
     :tagspace (list taggy)}  :tagspace-splitwithint     {:integer  '()
                                                          :exec (list (list
                                                            (make-tagspace {1 2 3 4})
                                                            (make-tagspace {5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(-22)
     :tagspace (list taggy)}  :tagspace-splitwithint     {:integer  '()
                                                          :exec (list (list
                                                            (make-tagspace {})
                                                            (make-tagspace {1 2 3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(77123)
     :tagspace (list taggy)}  :tagspace-splitwithint     {:integer  '()
                                                          :exec (list (list
                                                            (make-tagspace {1 2 3 4 5 6})
                                                            (make-tagspace {})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-splitwithfloat` pops an :float and a :tagspace and produces two new tagspaces (pushed to :exec)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:float '(3.0)
     :tagspace (list taggy)}  :tagspace-splitwithfloat     {:float  '()
                                                          :exec (list (list
                                                            (make-tagspace {1 2})
                                                            (make-tagspace {3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(3.1)
     :tagspace (list taggy)}  :tagspace-splitwithfloat     {:float  '()
                                                          :exec (list (list
                                                            (make-tagspace {1 2 3 4})
                                                            (make-tagspace {5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-22.0)
     :tagspace (list taggy)}  :tagspace-splitwithfloat     {:float  '()
                                                          :exec (list (list
                                                            (make-tagspace {})
                                                            (make-tagspace {1 2 3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(77123.7)
     :tagspace (list taggy)}  :tagspace-splitwithfloat     {:float  '()
                                                          :exec (list (list
                                                            (make-tagspace {1 2 3 4 5 6})
                                                            (make-tagspace {})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-tidywithints` pops two :integers and a :tagspace and cleans up the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:integer '(321 2)
     :tagspace (list taggy)}    :tagspace-tidywithints   {:integer  '()
                                                          :tagspace (list
                                                          (make-tagspace {2 2, 161 4, 321 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(-2 3)
     :tagspace (list taggy)}    :tagspace-tidywithints   {:integer  '()
                                                          :tagspace (list
                                                          (make-tagspace {-2 6, 0 4, 3 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(3 3)
     :tagspace (list taggy)}    :tagspace-tidywithints   {:integer  '()
                                                          :tagspace (list
                                                          (make-tagspace {3 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(-2 3)
     :tagspace (list (make-tagspace {}))}
                                :tagspace-tidywithints   {:integer  '()
                                                          :tagspace (list
                                                          (make-tagspace {}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-tidywithfloats` pops two :floats and a :tagspace and cleans up the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:float '(2.0 3.0)
     :tagspace (list taggy)}    :tagspace-tidywithfloats   {:float  '()
                                                          :tagspace (list
                                                          (make-tagspace {2.0 6, 2.5 4, 3.0 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(4.0 3.0)
     :tagspace (list taggy)}    :tagspace-tidywithfloats   {:float  '()
                                                          :tagspace (list
                                                          (make-tagspace {3.0 2, 3.5 4, 4.0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(3.0 3.0)
     :tagspace (list taggy)}    :tagspace-tidywithfloats   {:float  '()
                                                          :tagspace (list
                                                          (make-tagspace {3.0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:float '(-2.0 3.0)
     :tagspace (list (make-tagspace {}))}
                                :tagspace-tidywithfloats   {:float  '()
                                                          :tagspace (list
                                                          (make-tagspace {}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))