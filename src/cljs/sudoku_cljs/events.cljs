(ns sudoku-cljs.events
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.db :as db]
   [sudoku-cljs.game :as game]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::board
 (fn [db [_ pos val]]
   (-> db
       (assoc pos val)
       (update :invalid-pos conj pos)
       (game/update-invalids db pos))))
