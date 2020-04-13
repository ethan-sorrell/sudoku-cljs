(ns sudoku-cljs.board
  (:require
   [clojure.string :as string]))

;; board functions
(defn get-coord [row col]
  "take 1-indexed row, col and give coordinate string
   e.g. 1 1 -> a1"
  (str (char (+ 96 row)) col))

(defn get-xy [coord]
  "take coordinate string, give 1-indexed [row col]"
  (let [row-char (get coord 0)
        col-char (get coord 1)
        row (- (.charCodeAt row-char 0) 96)
        col (js/parseInt col-char 10)]
    [row col]))

(defn row-peers [coord]
  (let [[row-n col-n] (get-xy coord)]
    (for [col (range 1 10)]
      (get-coord row-n col))))

(defn col-peers [coord]
  (let [[row-n col-n] (get-xy coord)]
    (for [row (range 1 10)]
      (get-coord row col-n))))

(defn square-peers [coord]
  (let [[row-n col-n] (get-xy coord)
        col-3 (quot (dec col-n) 3)
        row-3 (quot (dec row-n) 3)
        start-col (inc (* 3 col-3))
        start-row (inc (* 3 row-3))]
    (for [row (range start-row (+ start-row 3))
          col (range start-col (+ start-col 3))]
      (get-coord row col))))

(defn all-peers [coord]
   (remove #(= % coord)
           (concat (square-peers coord)
                   (col-peers coord)
                   (row-peers coord))))

(defn neighborhood-peers [type]
  (case type
    :row row-peers
    :col col-peers
    :square square-peers))

(defn get-row [matrix coord]
  (map #(get matrix %) (row-peers coord)))

(defn get-square [matrix coord]
  (map #(get matrix %) (square-peers coord)))

(defn get-col [matrix coord]
  (map #(get matrix %) (col-peers coord)))

(defn get-neighborhood [matrix coord type]
  (case type
    :row (get-row matrix coord)
    :col (get-col matrix coord)
    :square (get-square matrix coord)))