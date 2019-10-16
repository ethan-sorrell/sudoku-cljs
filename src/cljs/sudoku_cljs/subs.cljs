(ns sudoku-cljs.subs
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.game :as game]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::cell
 (fn [db [_ pos]]
   (db pos)))

(re-frame/reg-sub
 ::db
 (fn [db _]
   db))

(re-frame/reg-sub
 ::invalid
 (fn [db _]
   (db :invalid-pos)))

#_(re-frame/reg-sub
 ::board
 (dissoc )
 (fn [db]
   (select-keys
    db
    (for [x (range 1 10)
          y (range 1 10)]
      (game/get-coord x y)))))
