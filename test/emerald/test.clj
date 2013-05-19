(ns emerald.test
  (:require [clojure.test.generative :refer :all]
            [clojure.test.generative.runner :as runner]
            [clojure.data.generators :as gen]
            [emerald.monad :refer :all]
            [emerald.syntax :refer :all]))

(defn monad []
  (gen/rand-nth ((juxt ->Right ->Left gen/list gen/vec) gen/anything)))

(defrecord Context [monad value])

(defn monad-ctx []
  (gen/rand-nth
   [(Context. ->Identity (gen/anything))
    (Context. ->Maybe (loop [value (gen/anything)]
                        (if (nil? value)
                          (recur (gen/anything))
                          value)))]))

(defn function []
  (gen/rand-nth [identity (constantly (gen/anything))]))

(defspec id
  (fn [m] (fmap m identity))
  [^{:tag `monad} m]
  (is (= % m)))

(defspec id-ctx
  (fn [{:keys [monad value]}] (for-m monad [x value] x))
  [^{:tag `monad-ctx} m]
  (is (= % (:value m))))

(defspec compose
  (fn [m f g] ((comp #(fmap % f) #(fmap % g)) m))
  [^{:tag `monad} m ^{:tag `function} f ^{:tag `function} g]
  (is (= % (fmap m (comp f g)))))

(defspec compose-ctx
  (fn [{:keys [monad value]} f g]
    (for-m monad [x (for-m monad [x value] (g x))] (f x)))
  [^{:tag `monad-ctx} m ^{:tag `function} f ^{:tag `function} g]
  (is (= % (for-m (:monad m) [x (:value m)] (f (g x))))))

(defspec left-unit
  (fn [{:keys [monad value]} f]
    (binding [*monad* monad]
      (extract (bind (point value) (comp point f)))))
  [^{:tag `monad-ctx} m ^{:tag `function} f]
  (is (= % (f (:value m)))))

(defspec right-unit
  (fn [{:keys [monad value]}]
    (binding [*monad* monad]
      (extract (bind (point value) point))))
  [^{:tag `monad-ctx} m]
  (is (= % (:value m))))

(defspec associative
  (fn [{:keys [monad value]}]
    (binding [*monad* monad]
      (= (extract (bind (bind (point value) point) point))
         (extract (bind (point value) #(bind (point %) point))))))
  [^{:tag `monad-ctx} m]
  (is %))

(runner/-main "test")
