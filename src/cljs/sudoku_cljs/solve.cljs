(ns sudoku-cljs.solve
  (:require
   [clojure.string :as string]
   [sudoku-cljs.board :as board]
   [sudoku-cljs.rules :as rules]))

(defn update-invalid-posns
  ;; TODO: could be modified to only check positions in "zone" of pos
  [db]
  (loop [new-invalids '()
         old-invalids (db :invalid-pos)]
    (if-let [[candidate-pos candidate-type] (first old-invalids)]
      (if (rules/valid-neighborhood? db candidate-pos candidate-type)
        (recur new-invalids (rest old-invalids))
        (recur (cons [candidate-pos candidate-type] new-invalids) (rest old-invalids)))
      (assoc db :invalid-pos new-invalids))))

(declare assign elim eliminate)

(defn elim [matrix from value]
  "Remove value from association with from in matrix"
  (assoc matrix from (string/replace (get matrix from) (re-pattern value) "")))

(defn candidate-locations [matrix pos value]
  "takes value and returns list of list of coords in unit which could contains value"
  (for [unit (conj []
                   (board/square-peers pos)
                   (board/row-peers pos)
                   (board/col-peers pos))]
    (for [loc unit
          :when (and (not (= loc pos))
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
           rem-peers (board/all-peers pos)]
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
