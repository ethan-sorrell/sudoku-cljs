(ns sudoku-cljs.game)

;;;;;;;;

(defn get-coord [row col]
  "take 1-indexed row, col and give coordinate string
   e.g. 1 1 -> a1"
  (str (char (+ 96 row)) col))

(defn get-xy [coord]
  "take coordinate string, give 1-indexed [row col]"
  (let [row-char (get coord 0)
        col-char (get coord 1)
        row (- (int row-char) 96)
        col (js/parseInt col-char 10)]
    [row col]))

(defn row-peers [coord]
  (let [[row-n col-n] (get-xy coord)]
    (for [col (range 1 10)]
      (get-coord row-n col))))

(defn square-peers [coord]
  (let [[row-n col-n] (get-xy coord)
        col-3 (quot (dec col-n) 3)
        row-3 (quot (dec row-n) 3)
        start-col (inc (* 3 col-3))
        start-row (inc (* 3 row-3))]
    (for [row (range start-row (+ start-row 3))
          col (range start-col (+ start-col 3))]
      (get-coord row col))))

(defn col-peers [coord]
  (let [[row-n col-n] (get-xy coord)]
    (for [row (range 1 10)]
      (get-coord row col-n))))

(defn get-row [matrix coord]
  (map #(get matrix %) (row-peers coord)))

(defn get-square [matrix coord]
  (map #(get matrix %) (square-peers coord)))

(defn get-col [matrix coord]
  (map #(get matrix %) (col-peers coord)))


;;;;;;;;
(defn contains-duplicates? [coll]
  "Take a collection of strings representing a row/col/vicinity"
  (let [elts (filter seq coll)]
    (= elts (distinct elts))))

(defn valid-cell?
  [matrix coord]
  (not-any? identity
   (map contains-duplicates?
        (map #(% matrix coord) [get-row get-col get-square]))))

(defn update-invalids
  ;; could be modified to only check positions in "zone" of pos
  [db pos]
  (loop [new-invalids '()
         old-invalids (get db :invalid-pos)]
    (if-let [candidate (first old-invalids)]
      (if (valid-cell? db candidate)
        (recur new-invalids (rest old-invalids))
        (recur (cons candidate new-invalids) (rest old-invalids)))
      (assoc db :invalid-pos new-invalids))))
