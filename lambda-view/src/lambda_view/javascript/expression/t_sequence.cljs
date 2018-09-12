;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-sequence
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; SequenceExpression
(defn render [node]
  (let [id (id-of node)
        expressions (get node "expressions")]
    [:div.sequence.expression (smart-box {:id            id
                                          :pair          :none
                                          :style         :mini
                                          :seperator     :comma
                                          :init-collapse false
                                          :init-layout   (if (> (count expressions) 5) "vertical" "horizontal")
                                          :auto-render   false} (doall (map (fn [child] (render-node-by-priority node child))
                                                                            expressions)))]))

(def demo ["a, b;"
           "a, b, c;"
           "a, (b, c);" ;; TODO BUG (layout)
           ])
