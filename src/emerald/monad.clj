(ns emerald.monad)

(def ^:dynamic *monad*)

(defn point [x] (*monad* x))

(defprotocol Functor
  (fmap [m f]))

(defprotocol Monad
  (bind [m f]))

(extend-type emerald.monad.Monad
  Functor
  (fmap [m f]
    (bind m (comp point f))))

(defprotocol MonadPlus
  (mfilter [m p]))

(defprotocol Comonad
  (extract [m]))

(defn fmap' [m a] (fmap m (constantly a)))
(defn bind' [m m'] (bind m (constantly m')))
(defn join [m] (bind m identity))

(defrecord Identity [value]
  Functor
  (fmap [m f] (bind m (comp ->Identity f)))
  Monad
  (bind [m f] (f value))
  Comonad
  (extract [m] value))

(extend-type nil
  Functor
  (fmap [m f] m)
  Monad
  (bind [m f] m)
  MonadPlus
  (mfilter [m p] m)
  Comonad
  (extract [m] m))
