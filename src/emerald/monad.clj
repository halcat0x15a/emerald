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

(defn >>= [m & ms] (reduce bind m ms))

(defn >>
  ([m m'] (bind m (constantly m')))
  ([m m' & ms] (reduce >> m (cons m' ms))))

(defn join [m] (bind m identity))

(defprotocol MonadPlus
  (mfilter [m p]))

(extend-type nil
  Functor
  (fmap [m f] m)
  Monad
  (bind [m f] m)
  MonadPlus
  (mfilter [m p] m))
