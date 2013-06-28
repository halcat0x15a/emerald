(ns emerald.syntax
  (:require [emerald.monad :refer :all]))

(extend-type java.lang.Object
  Functor
  (fmap [m f] (f m))
  Monad
  (bind [m f] (f m)))

(extend-type clojure.lang.ISeq
  Functor
  (fmap [m f] (map f m))
  Monad
  (bind [m f] (mapcat f m))
  MonadPlus
  (mfilter [m p] (filter p m)))

(defmacro with-m [m body]
  `(binding [*monad* ~m] ~body))

(defmacro for-m [[var val & exprs] expr]
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
          `(bind ~val (fn [~var] (for-m ~exprs ~expr))))))
