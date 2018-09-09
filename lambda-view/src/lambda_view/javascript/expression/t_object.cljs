;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-object
  (:require [lambda-view.javascript.expression.t-function :as fun-exp])
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              smart-box
                                              colon
                                              white-space
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]))

;; ObjectExpression
(defn object-expression-render [node]
  (let [id (id-of node)
        properties (get node "properties")]
    [:div.object.expression
     (smart-box {:id            id
                 :pair          :brace
                 :seperator     :comma
                 :init-collapse false
                 :init-layout   "vertical"} properties)]))

(defn property-render [node]
  (let [id (id-of node)
        method (get node "method")
        shorthand (get node "shorthand")
        computed (get node "computed")
        key (get node "key")
        value (get node "value")
        kind (get node "kind")]
    (cond
      ;; shorthand is simple: {k}
      shorthand
      [:div.property (render-node key)]

      ;; method will rendered as special form {method() {...}} not {method: function() {...}}
      method
      [:div.property
       ; TODO In this case, we can ignore "computed" right? It must be false?
       (fun-exp/special-render {:class "property-method"} value)]

      ;; getter/setter
      (or (= kind "get")
          (= kind "set"))
      [:div.property
       ; TODO In this case, we can ignore "computed" right? It must be false?
       (js-keyword kind)
       (white-space)
       (fun-exp/special-render {:class (str "property " kind)} value)]

      ;; not shorthand, not method ... okay, ordinary way
      (= kind "init")
      [:div.property
       (if (or (= kind "get")
               (= kind "set")) (list (js-keyword kind)
                                     (white-space)))
       (if computed (smart-box {:id            id
                                :pair          :bracket
                                :init-collapse true} [key])
                    (render-node key))
       (colon)
       (white-space-optional)
       (render-node value)]

      ;; Impossible?
      true
      (throw (js/Error. "unknown condition")))))

(defn spread-element [node]
  (let [id (id-of node)
        argument (get node "argument")]
    [:div.spread-element
     [:div.spread.operator "..."] (smart-box {:id            id
                                              :pair          :parenthesis
                                              :init-collapse false} [argument])]))

(def demo ["({});"
           "({k});"
           ;TODO not support yet ? "({k = 1})"
           "({...k});"
           "({key: value});"
           "({k1: v1, k2: v2});"
           "({[k1]: v1});"
           "({f(a,b) {1}});"
           "({f: () => 1});"
           "({f: function (a,b) {}});"
           "({get m() {1}});"
           "({set m(v) {1}});"
           ])
