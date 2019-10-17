(ns sudoku-cljs.game
  (:require
   [clojure.string :as string]))

;;;;;;;;

(defn get-coord [row col]
  "take 1-indexed row, col and give coordinate string
   e.g. 1 1 -> a1"
  (str (char (+ 96 row)) col))

(defn get-xy [coord]
  "take coordinate string, give 1-indexed [row col]"
  (let [row-char (get coord 0)
        col-char (get coord 1)
        ;;row (- (int row-char) 96)
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


;;;;;;;;;;;;;;;;;;;

(defn contains-duplicates? [coll]
  "Take a collection of strings representing a neighborhood"
  (let [elts (filter seq coll)]
    (not= elts (distinct elts))))

(defn valid-cell?
  [matrix coord]
  (not-any? identity
   (map #(not (contains-duplicates? %))
        (map #(% matrix coord) [get-row get-col get-square]))))

(defn valid-neighborhood?
  [db invalid-pos invalid-type]
  (not
   (contains-duplicates?
    (get-neighborhood db invalid-pos invalid-type))))

(defn update-invalids
  ;; could be modified to only check positions in "zone" of pos
  [db]
  (loop [new-invalids '()
         old-invalids (db :invalid-pos)]
    (if-let [[candidate-pos candidate-type] (first old-invalids)]
      (if (valid-neighborhood? db candidate-pos candidate-type)
        (recur new-invalids (rest old-invalids))
        (recur (cons [candidate-pos candidate-type] new-invalids) (rest old-invalids)))
      (assoc db :invalid-pos new-invalids))))
      ;;(assoc db :invalid-pos ["a3" :row]))))

;;;;;;;;;;; constraint propagation ;;;;;;;;;;;;;
(declare assign elim eliminate)

(defn elim [matrix from value]
  "Remove value from association with from in matrix"
  (assoc matrix from (string/replace (get matrix from) (re-pattern value) "")))

(defn candidate-locations [matrix pos value]
  "takes value and returns list of list of coords in unit which could contains value"
  (for [unit (conj []
                   (square-peers pos)
                   (row-peers pos)
                   (col-peers pos))]
    (for [loc unit
          :when (and
                 (not (= loc pos))
                 (string/includes? (get matrix loc) value))]
      loc)))

(defn propagate-in [matrix pos value]
  "Check if any unit containing pos is reduces to one possible location for a value"
  (when matrix
    (loop [result matrix
           rem-units (candidate-locations matrix pos value)]
      (if-not (seq rem-units)
        result
        (when-not (= 0 (count (first rem-units)))
          (if (= 1 (count (first rem-units)))
            (if-let [new-matrix
                     (assign result (first (first rem-units)) value)]
              (recur new-matrix (rest rem-units))
              false)
            (recur result (rest rem-units))))))))

(defn propagate-out [matrix pos]
  "Propagate a new constraint out from pos to its units"
  (let [value (get matrix pos)]
    (loop [result matrix
           rem-peers (all-peers pos)]
      (if-let [peer (first rem-peers)]
        (if (= peer pos)
          (recur result (rest rem-peers))
          (if-let [new-matrix (eliminate result peer value)]
            (recur new-matrix (rest rem-peers))
            false))
        result))))

(defn eliminate [matrix from value]
  "Elim and propagate"
  (if-not (string/includes? (get matrix from) value)
    matrix
    (let [new-matrix (elim matrix from value)
          remaining-count (count (get new-matrix from))]
      (cond
        (= 0 remaining-count) false ;; no remaining possible values
        ;; we have our value and need to propagate constraint
        (= 1 remaining-count) (propagate-in (propagate-out new-matrix from) from value)
        :else new-matrix))))

(defn assign [matrix pos value]
  "Eliminate all other values associated with pos then propagate"
  (when matrix
    (let [current_val (get matrix pos)
          other_vals (string/replace current_val (re-pattern value) "")]
      (loop [result matrix
             rem other_vals]
        (when result
          (if (= 0 (count rem))
            result
            (recur (eliminate result pos (str (first rem))) (rest rem))))))))
