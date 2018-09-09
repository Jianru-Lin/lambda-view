;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-labeled
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              colon]]))

;; LabeledStatement
(defn render [node]
  (let [label (get node "label")
        body (get node "body")]
    [:div {:class "labeled statement"}
     [:div {:class "label"} (render-node label)]
     (colon)
     (white-space-optional)
     [:div {:class "body"} (render-node body)]]))

(def demo ["label: 1"])