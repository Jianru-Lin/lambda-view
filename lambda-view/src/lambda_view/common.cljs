(ns lambda-view.common
  (:require [reagent.core :as reagent]
            [lambda-view.state :as state]))

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
;(defn pair [left right]
;  (fn [attr content]
;    (let [state (reagent/atom {:hover false})
;          hover (fn [] (swap! state assoc :hover true))
;          unhover (fn [] (swap! state assoc :hover false))
;          on-click (:on-click attr)]
;      (fn []
;        (println "pair render")
;        [:div.pair
;         (if-not (nil? left) [:div {:class          (str "pair left " (if (:hover @state) "hover"))
;                                    :on-mouse-enter hover
;                                    :on-mouse-leave unhover
;                                    :on-click       on-click} left])
;         content
;         (if-not (nil? right) [:div {:class          (str "pair right " (if (:hover @state) "hover"))
;                                     :on-mouse-enter hover
;                                     :on-mouse-leave unhover
;                                     :on-click       on-click} right])]))))

(defn pair [left right]
  (fn [attr content]
    (let [on-click (:on-click attr)]
      (fn []
        (println "pair render")
        [:div.pair
         [:div.pair.left {:on-click on-click} left]
         content
         [:div.pair.right {:on-click on-click} right]]))))

;; TODO find all reference
(def brackets (pair "[" "]"))

;; TODO find all reference (too many invalid invoke)
(def parenthese (pair "(" ")"))

;; TODO find all reference
;(def braces (pair "{" "}"))
(defn braces [attr content]
  (let [on-click (:on-click attr)]
    (fn []
      (println "brace pair render")
      [:div.pair
       [:div.pair.left {:on-click on-click} "{"]
       content
       [:div.pair.right {:on-click on-click} "}"]])))

(defn box [attr content]
  (let [id (:id attr)
        layout (state/get-layout id)]
    [:div {:class (str "box " layout)} content]))

(defn collapsed [attr]
  (let [id (:id attr)]
    [:div.collapsed {:on-click (fn [] (state/set-collapse! id false))} "..."]))

(defn toggle-collapse [state]
  (fn [] (swap! state assoc :collapse (not (:collapse @state)))))

(defn some-wrapper [attr content]
  [braces attr content])

(defn collapsable-box [attr content]
  (let [id (:id attr)
        collapse (state/get-collapse id)
        pair (:pair attr)
        pair-wrapper (cond
                       (= pair :brace) some-wrapper
                       (= pair :bracket) some-wrapper
                       (= pair :parenthesis) some-wrapper
                       true nil)]
    (println "collapsable-box" "collapse" collapse)
    (comment [pair-wrapper {:on-click (fn [] (state/toggle-collapse! id))} (if collapse [collapsed {:id id}]
                                                                                        [box {:id id} content])])
    [braces
     {:on-click (fn [] (state/toggle-collapse! id))}
     [:div "collapse=" (if collapse "true" "false")]]))
