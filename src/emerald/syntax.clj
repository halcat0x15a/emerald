(ns emerald.syntax
  (:require [emerald.monad :refer :all]))

(extend-type clojure.lang.IPersistentCollection
  Functor
  (fmap [m f] (map f m))
  Monad
  (bind [m f] (mapcat f m))
  MonadPlus
  (mfilter [m p] (filter p m)))

(defmacro for-m
  ([m exprs expr]
     `(binding [*monad* #(new ~m %)]
        (extract (for-m ~exprs ~expr))))
  ([[var val & exprs] expr]
     (let [[key expr' & exprs'] exprs]
       (cond (identical? key :if)
             `(for-m [~var (mfilter (point ~val) (fn [~var] ~expr'))
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
             `(fmap (point ~val) (fn [~var] ~expr))
             :else
             `(bind (point ~val) (fn [~var] (for-m ~exprs ~expr)))))))
