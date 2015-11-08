(ns push.types.base.char_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.char])
  (:use [push.util.type-checkers :only (boolean?)])
)


(fact "classic-char-type has :stackname ':char'"
  (:stackname classic-char-type) => :char)


(fact "classic-char-type has the correct :recognizer"
  (:recognizer classic-char-type) => (exactly char?))


(fact "classic-char-type has the expected :attributes"
  (:attributes classic-char-type) =>
    (contains #{:equatable :comparable :movable :string :visible}))


(fact "classic-char-type knows the :equatable instructions"
  (keys (:instructions classic-char-type)) =>
    (contains [:char-equal? :char-notequal?] :in-any-order :gaps-ok))


(fact "classic-char-type knows the :visible instructions"
  (keys (:instructions classic-char-type)) =>
    (contains [:char-stackdepth :char-empty?] :in-any-order :gaps-ok))


(fact "classic-char-type knows the :movable instructions"
  (keys (:instructions classic-char-type)) =>
    (contains [:char-shove :char-pop :char-dup :char-rotate :char-yank :char-yankdup :char-flush :char-swap] :in-any-order :gaps-ok))


(fact "classic-char-type knows a few things about characters"
  (keys (:instructions classic-char-type)) =>
  (contains [:char-letter? :char-digit? :char-whitespace? :char-lowercase? :char-uppercase?] :in-any-order :gaps-ok))