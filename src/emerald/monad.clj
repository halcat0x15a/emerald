(ns emerald.monad)

(def ^:dynamic *monad*)

(defn point [x] (*monad* x))

(defprotocol Functor
  (fmap [m f]))

(defn fmap' [m a] (fmap m (constantly a)))

(defprotocol Monad
  (bind [m f]))

(extend-type emerald.monad.Monad
  Functor
  (fmap [m f] (bind m (comp point f))))

(defn >>= [m & fs] (reduce bind m fs))

(defn >>
  ([m n] (bind m (constantly n)))
  ([m n & ms] (reduce >> m (cons n ms))))

(defn join [m] (bind m identity))

(defn collect [m & ms]
  (reduce (fn [m n] (bind m #(fmap n (partial conj %)))) (fmap m vector) ms))

(defn lift [f m & ms]
  (fmap (apply collect m ms) (partial apply f)))

(defprotocol MonadPlus
  (mfilter [m p]))

(extend-type nil
  Functor
  (fmap [m f] m)
  Monad
  (bind [m f] m)
  MonadPlus
  (mfilter [m p] m))
