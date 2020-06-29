(ns sudoku-cljs.db)

(def blank-board
  (into (hash-map) (for [col (range 1 10) row (range 1 10)] {(list row col) ""})))

(def default-db
  (merge
   {:invalid-cells '()
    :game-mode "learn"
    :show-output-panel? false}
   blank-board))


;; learn page
(def perfect-row
  (into (hash-map) (for [col (range 1 10)] {(list 1 col) (str col)})))

(def perfect-col
  (into (hash-map) (for [row (range 1 10)] {(list row 1) (str row)})))

(def perfect-square
  (into (hash-map) (for [i (range 1 10)
                         :let [row (inc (quot (dec i) 3))
                               col (inc (rem (dec i) 3))]]
                     {(list row col) (str i)})))
