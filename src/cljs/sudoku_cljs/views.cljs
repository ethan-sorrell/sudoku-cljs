(ns sudoku-cljs.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.game :as game]
   [sudoku-cljs.events :as events]))

(defn dispatch-board-change
  [pos val]
  (re-frame/dispatch [::events/board pos val]))

;; call dispatch-board-change on change
(defn cell-field
  [x y horiz vert]
  "hiccup markup for sudoku input cell"
  [:td
   {:class [(when horiz "horiz") (when vert "vert")]}
   [:input
    {:type "text"
     :name (game/get-coord y x)
     :size 1}]])

(defn table-row [col]
  "hiccup markup for row of sudoku input table"
  (letfn [(cell-row [col horiz vert]
            (into
             [:tr]
             (for [x (range 1 10)]
               (if (= (rem x 3) 0)
                 [cell-field x col horiz 1]
                 [cell-field x col horiz vert]))))]
    (if (= (rem col 3) 0)
      (cell-row col 1 nil)
      (cell-row col nil nil))))

(defn input-table [board]
  "hiccup markup for sudoku input table"
  [:div
   (into
    [:table {:border "2px solid;"}]
    (for [y (range 1 10)]
      [table-row y]))])

(defn board-panel []
  (let [board (re-frame/subscribe [::subs/board])
        board-state @board]
    [input-table @board]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]]))

#_(defn make-table []
  "make hiccup markup for sudoku input table"
  (into [:table {:border "2px solid;"}]
        (for [y (range 1 10)]
          (if (= (rem y 3) 0)
            (into
             [:tr]
             (for [x (range 1 10)]
               (if (= (rem x 3) 0)
                 [:td
                  {:style "border-right:2px solid; border-bottom:2px solid"}
                  (form/text-field {:size 1} (get-coord y x))]
                 [:td
                  {:style "border-bottom:2px solid"}
                  (form/text-field {:size 1} (get-coord y x))])))
            (into
             [:tr]
             (for [x (range 1 10)]
               (if (= (rem x 3) 0)
                 [:td
                  {:style "border-right:2px solid"}
                  (form/text-field {:size 1} (get-coord y x))]
                 [:td (form/text-field {:size 1} (get-coord y x))])))))))


#_(defn form-page
  [request]
  [:style
   "table, th, td {border-collapse: collapse;}"
   "th, td {padding: 3px;}"
   "td {text-align:center; width:48px; height:48px;}"]
  [:h1 "Input Your Sudoku Problem:"]
  ;; Input Table
  (form/form-to
   [:post "post-result"]
   (backend/make-table)
   (form/submit-button "Solve!")))
