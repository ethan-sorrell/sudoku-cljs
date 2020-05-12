(ns sudoku-cljs.rules
  (:require
   [clojure.string :as string]
   [sudoku-cljs.board :as board]))

(defn contains-duplicates? [coll]
  "Take a collection of strings representing a neighborhood"
  (let [elts (filter seq coll)]
    (not= elts (distinct elts))))

;; (defn valid-cell?
;;   [matrix coord]
;;   (not-any?
;;    identity
;;    (map contains-duplicates?
;;         (map #(% matrix coord) [board/get-row board/get-col board/get-square]))))

(defn valid-neighborhood?
  ;; neighborhood refers to 3x3 or 1x9 area with duplicity rules
  [db invalid-pos invalid-type]
  (not (contains-duplicates? (board/get-neighborhood db invalid-pos invalid-type))))

(defn conflicting-pos?
  "returns true if pos is part of an invalid neighborhood"
  [pos invalids]
  (some
   (fn [[invalid-pos neighborhood-type]]
     (some #(= % pos) ((board/neighborhood-peers neighborhood-type) invalid-pos)))
   invalids))
