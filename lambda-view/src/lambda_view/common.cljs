(ns lambda-view.common
  (:require [reagent.core :as reagent]))

(defn js-keyword [text]
  [:div.keyword text])

(defn white-space []
  [:div.white-space " "])

(defn comma []
  [:div.comma ","])

(defn colon []
  [:div.colon ":"])

(defn equal []
  [:div.equal "="])

(defn semicolon []
  [:div.semicolon ";"])

(defn asterisk []
  [:div.asterisk "*"])

(defn white-space-optional []
  [:div.white-space.optional " "])

;; [(pair "{" "}") content]
(defn pair [left right]
  (fn [attr content]
    (let [state (reagent/atom {:hover false})
          hover (fn [] (swap! state assoc :hover true))
          unhover (fn [] (swap! state assoc :hover false))
          on-click (:on-click attr)]
      (fn []
        [:div.pair
         (if-not (nil? left) [:div {:class          (str "pair left " (if (:hover @state) "hover"))
                                    :on-mouse-enter hover
                                    :on-mouse-leave unhover
                                    :on-click       on-click} left])
         content
         (if-not (nil? right) [:div {:class    (str "pair right " (if (:hover @state) "hover"))
                                     :on-mouse-enter hover
                                     :on-mouse-leave unhover
                                     :on-click on-click} right])]))))

(def brackets (pair "[" "]"))

(defn parenthese [content]
  (list [:div.parenthese.left "("] content [:div.parenthese.right ")"]))

(defn braces [content]
  (list [:div.brace.left "{"] content [:div.brace.right "}"]))

(defn block [attr content]
  (let [layout (:layout @(:state attr))]
    [:div {:class (str "block " layout)} content]))

(defn collapsed [attr content]
  (let [state (:state attr)
        collapse (:collapse @state)]
    (if collapse [:div.collapsed {:on-click (fn [] (swap! state assoc :collapse false))} "..."]
                 content)))
;[:div.collapsed {:on-click (fn [] (if-not (nil? on-expand) (on-expand)))} "..."]
