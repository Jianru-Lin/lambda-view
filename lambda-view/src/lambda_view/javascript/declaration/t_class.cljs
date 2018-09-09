;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-class
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.javascript.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   common-list
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!]]))

;; ClassDeclaration
(defn class-declaration-render [node]
  (let [id (get node "id")
        super-class (get node "superClass")
        body (get node "body")]
    [:div {:class "class declaration"}
     (js-keyword "class")
     (white-space)
     (render-node id)
     (if-not (nil? super-class) (list (white-space)
                                      (js-keyword "extends")
                                      (white-space)
                                      (render-node super-class)
                                      (white-space-optional)))
     (white-space-optional)
     (render-node body)]))

;; ClassBody
(defn class-body-render [node]
  (let [id (id-of node)
        body (get node "body")]
    (init-collapse! id true)
    [:div {:class "class-body"}
     (collapsable-box {:id   id
                       :pair :brace} (render-node-coll body))]))

;; MethodDefinition
(defn method-definition-render [node]
  (let [kind (get node "kind")
        static (get node "static")
        computed (get node "computed")
        key (get node "key")
        value (get node "value")]
    [:div {:class "method-definition"}
     (if static (list (js-keyword "static") (white-space)))
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
                                     (init-collapse! params-id false)
                                     (list (if async (list (js-keyword "async") ;; optional?
                                                           (white-space)))
                                           (if generator (list (asterisk)
                                                               (white-space))) ;; optional?
                                           (render-node key)
                                           (white-space-optional)
                                           (collapsable-box {:id params-id} (common-list {:id params-id} params))
                                           (white-space-optional)
                                           (render-node body)))
       (or (= kind "get")
           (= kind "set")) (let [fn-exp-node value
                                 params (get fn-exp-node "params")
                                 params-id (str (id-of node) ".params")
                                 body (get fn-exp-node "body")]
                             (init-collapse! params-id false)
                             (list (js-keyword kind)
                                   (white-space)
                                   (render-node key)
                                   (white-space-optional)
                                   (collapsable-box {:id params-id} (common-list {:id params-id} params))
                                   (white-space-optional)
                                   (render-node body))))]))

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