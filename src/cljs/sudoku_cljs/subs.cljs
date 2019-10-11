(ns sudoku-cljs.subs
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.game :as game]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::board
 (fn [db]
   (select-keys
    db
    (for [x (range 1 10)
          y (range 1 10)]
      (game/get-coord x y)))))
