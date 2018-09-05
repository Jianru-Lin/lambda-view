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

(defn operator [v]
  [:div.operator v])

;; Pair Factory
;; [(pair "{" "}") content]
(defn pair [left right]
  (fn [attr content]
    (let [id (:id attr)
          hover (if (state/get-hover id) "hover" nil)
          on-click (:on-click attr)]
      [:div.pair
       [:div.pair.left {:class          hover
                        :on-mouse-enter #(state/set-hover! id true)
                        :on-mouse-leave #(state/set-hover! id false)
                        :on-click       on-click} left]
       content
       [:div.pair.right {:class          hover
                         :on-mouse-enter #(state/set-hover! id true)
                         :on-mouse-leave #(state/set-hover! id false)
                         :on-click       on-click} right]])))

;; TODO find all reference
(def brackets (pair "[" "]"))

;; TODO find all reference (too many invalid invoke)
(def parenthese (pair "(" ")"))

;; TODO find all reference
(def braces (pair "{" "}"))

(defn box [attr content]
  (let [id (:id attr)
        style (if (= (:style attr) :mini) "mini")
        layout (state/get-layout id)]
    [:div {:class (str "box " layout " " style)} content]))

(defn collapsed [attr]
  (let [id (:id attr)
        style (if (= (:style attr) :mini) "mini")]
    [:div.collapsed {:class style
                     :on-click (fn [] (state/set-collapse! id false))} "..."]))

(defn toggle-collapse [state]
  (fn [] (swap! state assoc :collapse (not (:collapse @state)))))

(defn collapsable-box [attr content]
  (let [id (:id attr)
        collapse (state/get-collapse id)
        pair (:pair attr)
        pair-wrapper (cond
                       (= pair :brace) braces
                       (= pair :bracket) brackets
                       (= pair :parenthesis) parenthese
                       true parenthese)
        style (:style attr)
        on-click (fn [] (state/toggle-collapse! id))]
    [pair-wrapper {:id       id
                   :on-click on-click} (if (or (nil? content)
                                               (= 0 (count content))) nil
                                                                      (if collapse [collapsed {:id    id
                                                                                               :style style}]
                                                                                   [box {:id    id
                                                                                         :style style} content]))]))

(defn toggle-layout-element [id ele]
  [:div.toggle-layout {:on-click #(state/toggle-layout! id)} ele])
