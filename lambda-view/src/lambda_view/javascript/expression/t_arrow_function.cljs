;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-arrow-function
  (:use [lambda-view.javascript.render :only [render-node
                                              render-node-coll]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              asterisk
                                              smart-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; ArrowFunctionExpression
(defn render [node]
  (let [;; [NOTE]
        ;; ArrowFunctionExpression can not be used as generator, because:
        ;; MDN https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/Arrow_functions#Use_of_the_yield_keyword
        ;;     "The yield keyword may not be used in an arrow function's body
        ;;      (except when permitted within functions further nested within it).
        ;;      As a consequence, arrow functions cannot be used as generators."
        ;; check more: https://stackoverflow.com/questions/27661306/can-i-use-es6s-arrow-function-syntax-with-generators-arrow-notation
        ; generator (get node "generator")
        async (get node "async")
        params (get node "params")
        body (get node "body")]
    [:div.arrow-function.expression
     (if async (list (js-keyword "async")
                     (white-space)))
     (smart-box {:id            (str (id-of node) ".params")
                 :pair          :parenthesis
                 :seperator     :comma
                 :init-collapse false
                 :init-layout   "horizontal"} params)
     (white-space-optional)
     [:div "=>"]
     (white-space-optional)
     (render-node body)]))

(def demo ["(() => 1);"
           "(() => {1});"
           "(a => 1);"
           "((a) => 1);"
           "((a, b) => 1);"
           "(async () => 1);"
           ; [NOTE] Arrow function can not be used with generator
           ;"(*() => 1);"
           ;"(async *() => 1);"
           ])