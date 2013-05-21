(ns emerald.syntax
  (:require [emerald.monad :refer :all]))

(extend-type java.lang.Object
  Functor
  (fmap [m f] (fmap (point m) f))
  Monad
  (bind [m f] (bind (point m) f))
  MonadPlus
  (mfilter [m p] (mfilter (point m) p))
  Comonad
  (extract [m] m))

(extend-type clojure.lang.ISeq
  Functor
  (fmap [m f] (map f m))
  Monad
  (bind [m f] (mapcat f m))
  MonadPlus
  (mfilter [m p] (filter p m)))

(extend-type clojure.lang.IPersistentVector
  Functor
  (fmap [m f] (if (empty? m) m (conj (pop m) (f (peek m))))))

(defmacro with-m [m body]
  `(binding [*monad* ~m]
     (extract ~body)))

(defmacro for-m
  ([m exprs expr] `(with-m ~m (for-m ~exprs ~expr)))
  ([[var val & exprs] expr]
     (let [[key expr' & exprs'] exprs]
       (cond (identical? key :if)
             `(for-m [~var (mfilter ~val (fn [~var] ~expr'))
                      ~@exprs']
                     ~expr)
             (identical? key :let)
             (let [bindings (partition 2 expr')]
               `(for-m [[~var ~@(map first bindings)]
                        (for-m [~var ~val]
                               [~var ~@(map second bindings)])
                        ~@exprs']
                       ~expr))
             (empty? exprs)
             `(fmap ~val (fn [~var] ~expr))
             :else
             `(bind ~val (fn [~var] (for-m ~exprs ~expr)))))))
