(ns sudoku-cljs.learn
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.db :as db]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.board :as board]
   [sudoku-cljs.rules :as rules]
   [sudoku-cljs.solve :as solve]
   [sudoku-cljs.events :as events]))

(defn learn-page []
  [:div.row.bordered
   [:div.column "we start out with a blank board"]
   [:div.column (output-table db/blank-board)]])
