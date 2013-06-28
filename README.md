# Emerald

[![Build Status](https://travis-ci.org/halcat0x15a/emerald.png?branch=master)](https://travis-ci.org/halcat0x15a/emerald)

Protocol based Monad library

## Usage

### for-m

```clojure
(require '[emerald.monad :as m])
(require '[emerald.syntax :refer [for-m]])

;; List Monad
(for-m [a '(1 2 3)
        :let [x (inc a)]
        b '(2 4 6)
        :if (even? x)]
       [a b])

;; Identity and Maybe
(do-m (let! a ([1 2 3] 0))
      (let! b ({:foo 2 :bar 3} :baz))
      (+ a b))
```
