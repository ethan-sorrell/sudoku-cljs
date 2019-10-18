(ns sudoku-cljs.views
  (:require
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
  (letfn [(cell-row [horiz vert]
            (into
             [:tr]
             (for [x (range 1 10)]
               (if (= (rem x 3) 0)
                 (cell-field x col horiz 1)
                 (cell-field x col horiz vert)))))]
    (if (= (rem col 3) 0)
      [cell-row 1 nil]
      [cell-row nil nil])))

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

(defn convert-board [board]
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

(defn output-cell [board coord horiz vert]
  [:td
   {:class [(when horiz "horiz")
            (when vert "vert")]}
   (let [constraint-string (get board coord)
         constraints (map js/parseInt constraint-string)]
     [:div
      (filter (fn [item] (some #(= % item) constraints)) (range 1 10))
      ]
     #_(for [n (range 1 10)]
       [:div
        {:class [(when (contains? constraints n) "valid")]}
        #_(str n)
        (when (some #(= % n) constraints)
              (str n))])
     )])

(defn output-row [board row-n]
  (let [horiz (when (= (rem row-n 3) 0) true)]
    (into
     [:tr]
     (for [x (range 1 10)
           :let [coord (game/get-coord row-n x)]]
       (output-cell board coord horiz (when (= (rem x 3) 0) true))))))


(defn output-table [board]
  [:div
   [:table {:border "2px solid;"}
    (into
     [:tbody
      (for [row (range 1 10)]
        [output-row board row])])]])

(defn draw-output [db]
   (-> db
       (extract-board)
       (convert-board)
       (output-table)
       #_(str)))

;;;;;;;;;;;;;;;;;; top level ;;;;;;;;;;;;;;;;;;

(defn input-table []
  "hiccup markup for sudoku input table"
  [:div
   [:table {:border "2px solid;"}
    (into
     [:tbody]
     (for [y (range 1 10)]
       [table-row y]))]])

(defn output-panel []
  [:div
   [draw-output @(re-frame/subscribe [::subs/db])]])

(defn show-db []
  [:div
   [:p
    (str @(re-frame/subscribe [::subs/db]))]])

(defn show-invalids []
  [:div
   [:p
    (str @(re-frame/subscribe [::subs/invalid]))]])

(defn main-panel []
  "page for input table"
  [:div
   [:div.row
    [:div.column
     [input-table]]
    [:div.column
     [output-panel]]]
   [show-db]
   [show-invalids]])
