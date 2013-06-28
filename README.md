# Emerald

[![Build Status](https://travis-ci.org/halcat0x15a/emerald.png?branch=master)](https://travis-ci.org/halcat0x15a/emerald)

Protocol based Monad library

## Usage

### for-m

```clojure
(require '[emerald.syntax :refer [for-m]])

;; List Monad
(for-m [a '(1 2 3)
        :let [x (inc a)]
        b '(2 4 6)
        :if (even? x)]
       [a b])

;; Identity and Maybe
(for-m [a ([1 2 3] 0)
        b ({:foo 2 :bar 3} :baz)]
       (+ a b))
```

### >>=

```clojure
(require '[emerald.monad :refer [>>=]])

(>>= '(1 2 3) (partial repeat 2))
(>>= 0 inc inc (fn [x] (* x x)))
```
