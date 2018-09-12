;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-tagged-template
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; TaggedTemplateExpression
(defn render [node]
  (let [tag (get node "tag")
        quasi (get node "quasi")]
    [:div.tagged-template.expression
     (render-node-by-priority node tag)
     (render-node-by-priority node quasi)]))

(def demo ["tag`text`"])
