(ns lambda-view.javascript.common
  (:require [lambda-view.state :as state])
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.tag :only [id-of
                                mark-id!]]
        [lambda-view.state :only [init-layout!
                                  init-collapse!]]))

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

(def brackets (pair "[" "]"))

(def parenthese (pair "(" ")"))

(def braces (pair "{" "}"))

(def $braces (pair "${" "}"))

(def double-quote (pair "\"" "\""))

(def single-quote (pair "'" "'"))

(def back-quote (pair "`" "`"))

(def slashes (pair "/" "/"))

(defn box [attr content]
  (let [id (:id attr)
        style (if (= (:style attr) :mini) "mini")
        layout (state/get-layout id)]
    [:div {:class (str "box " layout " " style)} content]))

(defn collapsed [attr]
  (let [id (:id attr)
        style (if (= (:style attr) :mini) "mini")]
    [:div.collapsed {:class    style
                     :on-click (fn [] (state/set-collapse! id false))} "..."]))

(defn collapsable-box [attr content]
  (let [id (:id attr)
        collapse (state/get-collapse id)
        pair (:pair attr)
        pair-wrapper (cond
                       (= pair :brace) braces
                       (= pair :$brace) $braces
                       (= pair :bracket) brackets
                       (= pair :parenthesis) parenthese
                       (= pair :double-quote) double-quote
                       (= pair :single-quote) single-quote
                       (= pair :back-quote) back-quote
                       (= pair :slash) slashes
                       (= pair :none) nil
                       true (throw (js/Error. (str "Invalid pair option: " pair))))
        style (:style attr)
        on-click (fn [] (state/toggle-collapse! id))
        final-content (if (or (nil? content)
                              (= 0 (count content))) nil
                                                     (if collapse [collapsed {:id    id
                                                                              :style style}]
                                                                  [box {:id    id
                                                                        :style style} content]))]
    (if (= pair :none) final-content
                       [pair-wrapper {:id       id
                                      :on-click on-click} final-content])))

(defn toggle-layout-element [id ele]
  [:div.toggle-layout {:on-click #(state/toggle-layout! id)} ele])

(declare smart-box)

(defn common-list2 [attr coll]
  (let [id (:id attr)
        seperator (:seperator attr)
        auto-render (:auto-render attr)
        tail-idx (- (count coll) 1)]
    (doall (map-indexed (fn [idx e] (let [e-id (mark-id! e)]
                                      ^{:key e-id} [:div.box-element
                                                    (if (= auto-render false) e
                                                                              (render-node e))
                                                    ;; we tested the same condition twice to avoid using (list ...)
                                                    ;; because we don't want to generate key for each element in that list again!
                                                    (if (and (not= idx tail-idx)
                                                             (not= seperator :none)) (toggle-layout-element id (cond (= seperator :comma) (comma)
                                                                                                                     (= seperator :semicolon) (semicolon))))
                                                    (if (and (not= idx tail-idx)
                                                             (not= seperator :none)) (white-space-optional))]))
                        coll))))

(defn smart-box [attr coll]
  (let [id (:id attr)
        pair (:pair attr)
        init-layout-value (:init-layout attr)
        init-collapse-value (:init-collapse attr)
        seperator (:seperator attr)
        auto-render (:auto-render attr)
        style (:style attr)]
    ; id is required
    (if (nil? id) (throw (js/Error. (str "id of smart-box is nil: " attr " " coll))))
    ; default layout
    (if-not (nil? init-layout-value) (state/init-layout! id init-layout-value)
                                     (state/init-layout! id "horizontal"))
    ; default collapse
    (if-not (nil? init-collapse-value) (state/init-collapse! id init-collapse-value)
                                       (state/init-collapse! id false))
    ; render it
    (collapsable-box {:id    id
                      :pair  pair
                      :style style} (if (nil? coll) nil
                                                    (common-list2 {:id          id
                                                                   :seperator   seperator
                                                                   :auto-render auto-render} coll)))))
