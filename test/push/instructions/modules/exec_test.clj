(ns push.instructions.modules.exec_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.modules.exec])
  )



(fact "classic-exec-module has :stackname ':exec'"
  (:stackname classic-exec-module) => :exec)


(fact "classic-exec-module has the expected :attributes"
  (:attributes classic-exec-module) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "classic-exec-module knows the :equatable instructions"
  (keys (:instructions classic-exec-module)) =>
    (contains [:exec-equal? :exec-notequal?] :in-any-order :gaps-ok))


(fact "classic-exec-module does NOT know any :comparable instructions"
  (keys (:instructions classic-exec-module)) =not=> (contains [:exec<?]))


(fact "classic-exec-module knows the :visible instructions"
  (keys (:instructions classic-exec-module)) =>
    (contains [:exec-stackdepth :exec-empty?] :in-any-order :gaps-ok))


(fact "classic-exec-module knows the :movable instructions"
  (keys (:instructions classic-exec-module)) =>
    (contains [:exec-shove :exec-pop :exec-dup :exec-rotate :exec-yank :exec-yankdup :exec-flush :exec-swap] :in-any-order :gaps-ok))


(future-fact "classic-exec-module knows all the :exec-specific stuff"
  (keys (:instructions classic-exec-module)) =>
  (contains [] :in-any-order :gaps-ok))
