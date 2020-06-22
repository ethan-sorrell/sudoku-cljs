;; all of the markup that forms our page
(ns sudoku-cljs.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.db :as db]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.board :as board]
   [sudoku-cljs.rules :as rules]
   [sudoku-cljs.solve :as solve]
   [sudoku-cljs.events :as events]))

(defn board-change
  [pos event]
  (re-frame/dispatch [::events/board pos (-> event .-target .-value)]))

(defn toggle-button []
  (let [show-output-panel (re-frame/subscribe [::subs/show-output-panel])]
    [:div
     [:input
      {:type "checkbox"
       :id "is-shown"
       :checked @show-output-panel
       :on-change #(re-frame/dispatch [::events/toggle-output-panel (not @show-output-panel)])}]
     [:label {:for "is-shown"} "Show Board Constraints"]]))

(defn cell-field
  [x y]
  "hiccup markup for sudoku input cell"
  (let [pos (list y x)]
    [:td
     {:class ["cell"
              (when (rules/conflicting-pos?
                     pos @(re-frame/subscribe [::subs/invalid]))
                "invalid")]}
     [:input
      {:type "text"
       :name pos
       :on-blur #(when % (board-change pos %))
       :size 1}]]))

(defn table-row [col]
  "hiccup markup for row of sudoku input table"
  (into
   [:tr.cell-row]
   (for [x (range 1 10)]
     (cell-field x col))))


(defn extract-board [db]
  "extracts only the board cells from the state db"
  (map #(vector % (db %)) board/coord-set))

(defn output-cell [board coord]
  [:td.cell
   (let [constraint-string (get board coord)
         constraints (map js/parseInt constraint-string)]
     [:div
      (interleave
       (repeat " ")
       (filter (fn [item] (some #(= % item) constraints)) (range 1 10)))])])

(defn output-row [board row-n]
  (into
   [:tr.cell-row]
   (for [x (range 1 10)
         :let [coord (list row-n x)]]
     (output-cell board coord))))

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
       (output-table)))

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

(defn header-bar []
  [:div.header [:h1 "Sudoku Helper"]])

(defn mode-bar []
  [:div.mode-bar
   [:a.mode-cell
    {:on-click #(re-frame/dispatch [::events/change-mode "learn"])}
    "learn"]
   [:a.mode-cell
    {:on-click #(re-frame/dispatch [::events/change-mode "play"])}
    "play"]])

(defn learn-page []
  [:div.row.bordered
   [:div.column "we start out with a blank board"]
   [:div.column (output-table db/blank-board)]])

(defn main-panel []
  "page for input table"
  [:div
   [header-bar]
   [mode-bar]
   (let [game-mode @(re-frame/subscribe [::subs/game-mode])]
     (case game-mode
       "play" [:div
               [:div.row.bordered
                [:div.column
                 [toggle-button]]
                [:div.row
                 [:div.column
                  [input-table]]
                 [:div.column
                  [output-panel]]]]
               #_[show-db]
               [show-invalids]]
       "learn" [learn-page]))])
