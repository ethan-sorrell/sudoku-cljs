(ns sudoku-cljs.db)

(def blank-board
  (into (hash-map) (for [row (range 1 10) col (range 1 10)] {(list col row) ""})))

(def default-db
  (merge
    {:invalid-cells '()}
    blank-board))
