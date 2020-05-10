;; all of the markup that forms our page
(ns sudoku-cljs.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.board :as board]
   [sudoku-cljs.rules :as rules]
   [sudoku-cljs.solve :as solve]
   [sudoku-cljs.events :as events]))

(defn board-change
  [pos event]
  (re-frame/dispatch [::events/board pos (-> event .-target .-value)]))

(defn cell-field
  [x y horiz vert]
  "hiccup markup for sudoku input cell"
  (let [pos (list y x)]
    [:td
     {:class [(when horiz "horiz") (when vert "vert")
              (when (rules/conflicting-pos?
                     pos
                     @(re-frame/subscribe [::subs/invalid]))
                "invalid")]}
     [:input
      {:type "text"
       :name pos
       :on-blur #(when % (board-change pos %))
       :size 1}]]))

(defn toggle-button []
  (let [show-output-panel (re-frame/subscribe [::subs/show-output-panel])]
    [:div
     [:input
      {:type "checkbox"
       :value @show-output-panel
       :on-change #(re-frame/dispatch [::events/toggle-output-panel (not @show-output-panel)])}]
     [:label "Show Board Constraints"]]))

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

(defn extract-board [db]
  "extracts only the board cells from the state db"
  (map #(vector % (db %)) board/coord-set)
  #_(map db board/coord-set))

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
              (str n))]))])

(defn output-row [board row-n]
  (let [horiz (when (= (rem row-n 3) 0) true)]
    (into
     [:tr]
     (for [x (range 1 10)
           :let [coord (list row-n x)]]
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
       (solve/constrain-board)
       (output-table)
       #_(str)))

(defn input-table []
  "hiccup markup for sudoku input table"
  [:div
   [:table {:border "2px solid;"}
    (into
     [:tbody]
     (for [y (range 1 10)]
       [table-row y]))]])

(defn output-panel []
  (let [is-shown @(re-frame/subscribe [::subs/show-output-panel])]
    (when is-shown
      [:div
       [draw-output @(re-frame/subscribe [::subs/db])]])))

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
     [toggle-button]]
    [:div.column
     [input-table]]
    [:div.column
     [output-panel]]]
   #_[show-db]
   #_[show-invalids]])
