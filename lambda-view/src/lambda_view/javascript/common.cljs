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
    [:div.collapsed {:class    style
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
                       (= pair :none) nil
                       true (throw (.js/Error (str "Invalid pair option: " pair))))
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

(defn common-list [attr coll]
  (if (nil? coll) nil
                  (let [id (:id attr)
                        sep (cond
                              (= (:sep attr) :comma) (comma)
                              true (comma))
                        tail-idx (- (count coll) 1)]
                    (state/init-layout! id (if (> tail-idx 4) "vertical" "horizontal"))
                    (doall (map-indexed (fn [idx e] ^{:key (id-of e)} [:div.box-element
                                                                       (render-node e)
                                                                       (if (not= idx tail-idx) (list (toggle-layout-element id sep)
                                                                                                     (white-space-optional)))])
                                        coll)))))

(declare smart-box)

(defn render-exp-node [exp-node parent-exp-node]
  ; TODO Compare Priority...
  (mark-id! exp-node)
  (if (or (nil? parent-exp-node)
          (= "Identifier" (get exp-node "type"))
          (= "Literal" (get exp-node "type"))) (render-node exp-node)
                                               (let [id (id-of exp-node)]
                                                 (smart-box {:id            id
                                                             :pair          :parenthesis
                                                             :init-layout   "horizontal"
                                                             :init-collapse false} [exp-node]))))

(defn common-list2 [attr coll]
  (let [id (:id attr)
        seperator (:seperator attr)
        tail-idx (- (count coll) 1)]
    (doall (map-indexed (fn [idx e] (let [e-id (mark-id! e)]
                                      ^{:key e-id} [:div.box-element
                                                    (render-node e)
                                                    ;; we tested the same condition twice to avoid using (list ...)
                                                    ;; because we don't want to generate key for each element in that list again!
                                                    (if (not= idx tail-idx) (toggle-layout-element id (comma)))
                                                    (if (not= idx tail-idx) (white-space-optional))
                                                    ;; like this... :(
                                                    #_(if (not= idx tail-idx) (list (with-meta (toggle-layout-element id (cond (= seperator :comma) (comma)
                                                                                                                               true (comma))) {:key (str e-id ".tg")})
                                                                                    (with-meta (white-space-optional) {:key (str e-id ".sp")})))]))
                        coll))))

(defn smart-box [attr coll]
  (let [id (:id attr)
        pair (:pair attr)
        init-layout-value (:init-layout attr)
        init-collapse-value (:init-collapse attr)
        seperator (:seperator attr)]
    ; id is required
    (if (nil? id) (throw (.js/Error (str "id of smart-box is nil: " attr " " coll))))
    ; default layout
    (if-not (nil? init-layout-value) (state/init-layout! id init-layout-value)
                                     (state/init-layout! id "horizontal"))
    ; default collapse
    (if-not (nil? init-collapse-value) (state/init-collapse! id init-collapse-value)
                                       (state/init-collapse! id false))
    ; render it
    (collapsable-box {:id   id
                      :pair pair} (if (nil? coll) nil
                                                  (common-list2 {:id        id
                                                                 :seperator seperator} coll)))))
