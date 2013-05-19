# emerald

Protocol based Monad library

## Usage

```clojure
(require '[emerald.monad :as m])
(require '[emerald.syntax :refer [for-m]])

; List Monad
(for-m [a [1 2 3]
        :let [b (inc a)]
        b [2 4 6]
        :if (< a b)]
       [a b])

; Maybe Monad
(for-m m/->Maybe
       [a ([1 2 3] 0)
        b ({:foo 2 :bar 3} :baz)
        :if (< a b)]
       (+ a b))
```
