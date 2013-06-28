(ns emerald.test
  (:require [clojure.test.generative :refer :all]
            [clojure.test.generative.runner :as runner]
            [clojure.data.generators :as gen]
            [emerald.monad :refer :all]
            [emerald.syntax :refer :all]))

(defn object []
  (if-let [value (gen/scalar)]
    value
    (object)))

(defn mplus []
  (gen/rand-nth [(constantly nil) list]))

(defn monad []
  (gen/rand-nth [(mplus) (constantly (object))]))

(defn functor []
  (gen/rand-nth
   ((juxt vector (monad)) (gen/anything))))

(defn function []
  (gen/rand-nth [identity (constantly (object))]))

(defspec id
  (fn [m] (fmap m identity))
  [^{:tag `functor} m]
  (is (= % m)))

(defspec compose
  (fn [m f g]
    [(fmap m (comp f g))
     ((comp #(fmap % f) #(fmap % g)) m)])
  [^{:tag `functor} m ^{:tag `function} f ^{:tag `function} g]
  (is (apply = %)))

(defspec left
  (fn [m a f]
    [(bind (m a) (comp m f))
     (m (f a))])
  [^{:tag `monad} m ^anything a ^{:tag `function} f]
  (is (apply = %)))

(defspec right
  (fn [m a] (bind (m a) m))
  [^{:tag `monad} m ^anything a]
  (is (= % (m a))))

(defspec associative
  (fn [m a f g]
    [(bind (m a) #(bind (m (f %)) (comp m g)))
     (bind (bind (m a) (comp m f)) (comp m g))])
  [^{:tag `monad} m ^anything a
   ^{:tag `function} f ^{:tag `function} g]
  (is (apply = %)))

(defspec zero
  (fn [m a f]
    [(bind (mfilter (m a) (constantly false)) (comp m f))
     (mfilter (m a) (constantly false))])
  [^{:tag `mplus} m ^anything a ^{:tag `function} f]
  (is (apply = %)))
