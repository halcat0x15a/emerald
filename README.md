# Emerald

Protocol based Monad library

## Usage

### for-m

```clojure
(require '[emerald.monad :as m])
(require '[emerald.syntax :refer [for-m]])

;; List Monad
(for-m [a [1 2 3]
        :let [b (inc a)]
        b [2 4 6]
        :if (< a b)]
       [a b])

;; Identity Monad and Maybe Monad
(for-m ->Identity
       [a ([1 2 3] 0)
        b ({:foo 2 :bar 3} :baz)
        :if (< a b)]
       (+ a b))
```
