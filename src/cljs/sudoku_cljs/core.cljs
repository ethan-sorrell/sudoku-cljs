(ns sudoku-cljs.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [sudoku-cljs.events :as events]
   [sudoku-cljs.views :as views]
   [sudoku-cljs.db :as db] ;; probably temporary
   [sudoku-cljs.config :as config]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render
   ;; [views/main-panel]
   ;; [:table {:class ".horiz"} [:td [:input {:type "text" :name "a1" :size 1}]]]
   ;; [:table (views/cell-field 1 1 nil nil)]
   (views/input-table db/default-db)
   (.getElementById js/document "app")))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
