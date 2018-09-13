;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-class
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              asterisk
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ClassDeclaration
(defn class-declaration-render [node]
  (let [id (get node "id")
        super-class (get node "superClass")
        body (get node "body")]
    [:div {:class "class declaration"}
     (js-keyword "class")
     (white-space)
     (render-node id)
     (if-not (nil? super-class) [:div
                                 (white-space)
                                 (js-keyword "extends")
                                 (white-space)
                                 (render-node super-class)])
     (white-space-optional)
     (render-node body)]))

;; ClassBody
(defn class-body-render [node]
  (let [id (id-of node)
        body (get node "body")]
    [:div {:class "class-body"}
     (smart-box {:id          id
                 :pair        :brace
                 :seperator   :none
                 :init-layout "vertical"} body)]))

;; MethodDefinition
(defn method-definition-render [node]
  (let [kind (get node "kind")
        static (get node "static")
        computed (get node "computed")
        key (get node "key")
        value (get node "value")]
    [:div {:class "method-definition"}
     (if static [:div (js-keyword "static") (white-space)])
     (cond
       ;; Constructor or Method
       (or (= kind "method")
           (= kind "constructor")) (let [fn-exp-node value
                                         _id (get fn-exp-node "id") ; always nil
                                         generator (get fn-exp-node "generator")
                                         _expression (get fn-exp-node "expression") ;; useless
                                         async (get fn-exp-node "async")
                                         params (get fn-exp-node "params")
                                         params-id (str (id-of node) ".params")
                                         body (get fn-exp-node "body")]
                                     [:div
                                      (if async [:div
                                                 (js-keyword "async") ;; optional?
                                                 (white-space)])
                                      (if generator [:div
                                                     (asterisk)
                                                     (white-space)]) ;; optional?
                                      (render-node key)
                                      (white-space-optional)
                                      (smart-box {:id          params-id
                                                  :pair        :parenthesis
                                                  :seperator   :comma
                                                  :init-layout (if (> (count params) 4) "vertical" "horizontal")} params)
                                      (white-space-optional)
                                      (render-node body)])
       (or (= kind "get")
           (= kind "set")) (let [fn-exp-node value
                                 params (get fn-exp-node "params")
                                 params-id (str (id-of node) ".params")
                                 body (get fn-exp-node "body")]
                             [:div
                              (js-keyword kind)
                              (white-space)
                              (render-node key)
                              (white-space-optional)
                              (smart-box {:id          params-id
                                          :pair        :parenthesis
                                          :seperator   :comma
                                          :init-layout (if (> (count params) 4) "vertical" "horizontal")} params)
                              (white-space-optional)
                              (render-node body)]))]))

(def demo [;"class a1 {}"
           ;"class a2 extends b2 {}"
           ;"class a3 { constructor(a, b) {1} }"
           ;"class a4 { m1(a, b) {1} async m2(a, b) {2} * m3 (a, b) {3} async * m4(a, b) {4} }"
           "class a5 { get m() {1} }"
           "class a6 { static get m() {1} }"
           "class a7 { set m(v) {1} }"
           "class a8 { static set m(v) {1} }"
           ;"class a9 { static m(a, b) {1} }"
           ])