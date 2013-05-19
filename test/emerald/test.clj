(ns emerald.test
  (:require [clojure.test.generative :refer :all]
            [clojure.test.generative.runner :as runner]
            [clojure.data.generators :as gen]
            [emerald.monad :refer :all]
            [emerald.syntax :refer :all]))

(defn monad []
  (gen/rand-nth
   ((juxt ->Right
          ->Left
          gen/list
          gen/vec
          (constantly nil))
    (gen/anything))))

(defrecord Context [monad value])

(defn monad-ctx []
  (gen/rand-nth
   [(Context. ->Identity (gen/anything))]))

(defn function []
  (gen/rand-nth [identity (constantly (gen/anything))]))

(defspec id
  (fn [m] (fmap m identity))
  [^{:tag `monad} m]
  (is (= % m)))

(defspec id-ctx
  (fn [{:keys [monad value]}]
    (with-m monad (fmap (point value) identity)))
  [^{:tag `monad-ctx} m]
  (is (= % (:value m))))

(defspec compose
  (fn [m f g] ((comp #(fmap % f) #(fmap % g)) m))
  [^{:tag `monad} m ^{:tag `function} f ^{:tag `function} g]
  (is (= % (fmap m (comp f g)))))

(defspec compose-ctx
  (fn [{:keys [monad value]} f g]
    (with-m monad ((comp #(fmap % f) #(fmap % g)) (point value))))
  [^{:tag `monad-ctx} m ^{:tag `function} f ^{:tag `function} g]
  (is (= % (with-m (:monad m) (fmap (point (:value m)) (comp f g))))))

(defspec left-unit
  (fn [{:keys [monad value]} f]
    (with-m monad (bind (point value) (comp point f))))
  [^{:tag `monad-ctx} m ^{:tag `function} f]
  (is (= % (f (:value m)))))

(defspec right-unit
  (fn [{:keys [monad value]}]
    (with-m monad (bind (point value) point)))
  [^{:tag `monad-ctx} m]
  (is (= % (:value m))))

(defspec associative
  (fn [{:keys [monad value]}]
    (with-m monad (bind (point value) #(bind (point %) point))))
  [^{:tag `monad-ctx} m]
  (is (= % (with-m (:monad m)
             (bind (bind (point (:value m)) point) point)))))

(runner/-main "test")
