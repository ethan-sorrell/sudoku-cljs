(ns sudoku-cljs.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.events :as events]
))

(defn dispatch-board-change
  [pos val]
  (rf/dispatch [::events/board pos val]))

;; call dispatch-board-change on change
(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     ]))


