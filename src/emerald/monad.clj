(ns emerald.monad)

(def ^:dynamic *monad* identity)

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

(deftype Identity [value]
  Monad
  (bind [m f]
    (f value))
  Comonad
  (extract [m] value))

(deftype Maybe [value]
  Monad
  (bind [m f] (if-not (nil? value) (f value) m))
  MonadPlus
  (mfilter [m p] (if (p value) value))
  Comonad
  (extract [m] value))

(deftype Right [value]
  Functor
  (fmap [m f] (Right. (f value)))
  Monad
  (bind [m f] (f value)))

(deftype Left [value]
  Functor
  (fmap [m f] m)
  Monad
  (bind [m f] m))
