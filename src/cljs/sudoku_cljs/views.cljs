(ns sudoku-cljs.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.game :as game]
   [sudoku-cljs.views-helper :as helper]
   [sudoku-cljs.events :as events]))

(defn input-table []
  "hiccup markup for sudoku input table"
  [:div
   [:table {:border "2px solid;"}
    (into
     [:tbody]
     (for [y (range 1 10)]
       [helper/table-row y]))]])

(defn output-table []
  (let [db @(re-frame/subscribe [::subs/db])]
    [:div
     ;"test"
     (helper/draw-output db)]))

(defn show-db []
  [:div
   [:p
    (str @(re-frame/subscribe [::subs/db]))]])

(defn show-invalids []
  [:div
   [:p
    (str @(re-frame/subscribe [::subs/invalid]))]])

(defn input-panel []
  "page for input table"
  [:div
   [:div.row
    [:div.column
     [input-table]]
    [:div.column
     [output-table]]]
   [show-db]
   [show-invalids]])

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
