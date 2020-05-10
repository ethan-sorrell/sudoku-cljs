(ns sudoku-cljs.subs
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.board :as board]))

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
   (db :invalid-cells)))

(re-frame/reg-sub
 ::show-output-panel
 (fn [db _]
   (db :show-output-panel?)))

#_(re-frame/reg-sub
 ::board
 (dissoc)
 (fn [db]
   (select-keys
    db
    (for [x (range 1 10)
          y (range 1 10)]
      (list x y)))))
