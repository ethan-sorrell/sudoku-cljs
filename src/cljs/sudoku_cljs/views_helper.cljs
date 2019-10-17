(ns sudoku-cljs.views-helper
  (:require
   [clojure.string :as string]
   [re-frame.core :as re-frame]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.game :as game]
   [sudoku-cljs.events :as events]))


(defn board-change
  [pos event]
  (re-frame/dispatch [::events/board pos (-> event .-target .-value)]))

(defn conflicting-pos?
  [pos invalids]
  (loop [rem invalids]
    (if (not (seq rem))
      false
      (let [[invalid-pos type] (first rem)]
        (if (some #(= % pos) ((game/neighborhood-peers type) invalid-pos))
          true
          (recur (rest rem)))))))

(defn cell-field
  [x y horiz vert]
  "hiccup markup for sudoku input cell"
  (let [pos (game/get-coord y x)]
    [:td
     {:class [(when horiz "horiz") (when vert "vert")
              (when (conflicting-pos?
                     pos
                     @(re-frame/subscribe [::subs/invalid]))
                "invalid")]}
     [:input
      {:type "text"
       :name pos
       :on-blur #(when % (board-change pos %))
       :size 1}]]))

(defn table-row [col]
  "hiccup markup for row of sudoku input table"
  (letfn [(cell-row [col horiz vert]
            (into
             [:tr]
             (for [x (range 1 10)]
               (if (= (rem x 3) 0)
                 (cell-field x col horiz 1)
                 (cell-field x col horiz vert)))))]
    (if (= (rem col 3) 0)
      [cell-row col 1 nil]
      [cell-row col nil nil])))

;;;;;;;;;;;;;;;;; output helpers ;;;;;;;;;;;;;;;


;;;;;;;;;;; markup and other helpers ;;;;;;;;;;;

(def coord-set
  (for [y (range 1 10)
        x (range 1 10)
        :let [coord (game/get-coord y x)]]
    coord))

(def uninitialized-cboard
  (into
   {}
   (map #(vector % "123456789") coord-set)))

(defn extract-board [db]
  (map #(vector % (db %)) coord-set)
  #_(map db coord-set))

(defn convert-table [board]
  "convert from partially-filled in solution to description of constraints"
  (loop [result uninitialized-cboard
         rem board]
    (when result
      (if-not (seq rem)
        result
        (let [pair (first rem)
              coord (first pair)
              vals (second pair)]
          (if-not (seq vals)
            (recur result (rest rem))
            (recur (game/assign result coord vals) (rest rem))))))))

(defn draw-output [db]
   (-> db
       (extract-board)
       (convert-table)
       (str)))
