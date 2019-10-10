(ns sudoku-cljs.game)

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
