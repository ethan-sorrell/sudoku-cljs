(ns sudoku-cljs.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku-cljs.subs :as subs]
   [sudoku-cljs.game :as game]
   [sudoku-cljs.events :as events]))

(defn board-change
  [pos event]
  (re-frame/dispatch [::events/board pos (-> event .-target .-value)]))

(defn conflicting-pos?
  [pos invalids]
  (loop [rem invalids]
    (if (not (seq rem))
      false
      (let [[invalid-pos type] (first rem)]
        (.log js/console (str (some #(= pos) ((game/neighborhood-peers type) invalid-pos))
                              " : "
                              ((game/neighborhood-peers type) invalid-pos)) " and " pos)
       ;; (.log js/console (str (contains? ((game/neighborhood-peers type) invalid-pos)  pos)))
        (if (some #(= pos) ((game/neighborhood-peers type) invalid-pos))
          true
          (recur (rest rem)))))))

#_(defn conflicting-pos? [pos invalids]
  (loop [rem invalids]
    (if (not (seq rem))
      nil
      (let [[invalid-pos type] (first rem)]
        #_(recur (rest rem))))))
        ;; #_(.log js/console (str ((game/neighborhood-peers type) invalid-pos)))))))
  ;;(.log js/console (str invalids))
  ;;(contains? (map first invalids) pos))

(defn cell-field
  [x y horiz vert]
  "hiccup markup for sudoku input cell"
  (let [pos (game/get-coord y x)]
    [:td
     {:class [(when horiz "horiz") (when vert "vert")
              (when (conflicting-pos?
                     pos
                     @(re-frame/subscribe [::subs/invalid]))
                "invalid")]}
     [:input
      {:type "text"
       :name pos
       :on-blur #(when % (board-change pos %))
       :size 1}]]))


#_(defn cell-field
  [x y horiz vert]
  "hiccup markup for sudoku input cell"
  (let [pos (game/get-coord y x)]
    [:td
     {:class [(when horiz "horiz") (when vert "vert")
              (when (conflicting-pos?
                     pos
                     @(re-frame/subscribe [::subs/invalid]))
                "invalid")]}
     [:input
      {:type "text"
       :name pos
       :value @(re-frame/subscribe [::subs/cell pos])
       :on-change #(re-frame/dispatch (board-change pos %))
       :size 1}]]))

(defn table-row [col]
  "hiccup markup for row of sudoku input table"
  (letfn [(cell-row [col horiz vert]
            (into
             [:tr]
             (for [x (range 1 10)]
               (if (= (rem x 3) 0)
                 (cell-field x col horiz 1)
                 (cell-field x col horiz vert)))))]
    (if (= (rem col 3) 0)
      [cell-row col 1 nil]
      [cell-row col nil nil])))

(defn input-table []
  "hiccup markup for sudoku input table"
  [:div
   [:table {:border "2px solid;"}
    (into
     [:tbody]
     (for [y (range 1 10)]
       [table-row y]))]])

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
   [input-table]
   [show-db]
   [show-invalids]])

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
