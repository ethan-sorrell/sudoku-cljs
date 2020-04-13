(ns sudoku-cljs.rules
  (:require
   [clojure.string :as string]
   [sudoku-cljs.board :as board]))

(defn contains-duplicates? [coll]
  "Take a collection of strings representing a neighborhood"
  (let [elts (filter seq coll)]
    (not= elts (distinct elts))))

(defn valid-cell?
  [matrix coord]
  (not-any? identity
   (map #(not (contains-duplicates? %))
        (map #(% matrix coord) [board/get-row board/get-col board/get-square]))))

(defn valid-neighborhood?
  [db invalid-pos invalid-type]
  (not
   (contains-duplicates?
    (board/get-neighborhood db invalid-pos invalid-type))))

(defn conflicting-pos?
  [pos invalids]
  (loop [rem invalids]
    (if (not (seq rem))
      false
      (let [[invalid-pos type] (first rem)]
        (if (some #(= % pos) ((board/neighborhood-peers type) invalid-pos))
          true
          (recur (rest rem)))))))

