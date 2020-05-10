(ns sudoku-cljs.events
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.db :as db]
   [sudoku-cljs.solve :as solve]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::toggle-output-panel
 (fn [db [_ bool]]
   (assoc db :show-output-panel? bool)))

(re-frame/reg-event-db
 ::board
 (fn [db [_ pos val]]
   (-> db
       (assoc pos val)
       (update :invalid-cells conj [pos :row])
       (update :invalid-cells conj [pos :col])
       (update :invalid-cells conj [pos :square])
       (solve/update-invalid-posns))))
