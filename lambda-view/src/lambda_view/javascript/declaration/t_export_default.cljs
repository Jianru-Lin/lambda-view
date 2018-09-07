;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-export-default
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   common-list
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!]]))

;; ExportDefaultDeclaration
(defn render [node]
  [:div {:class "export-default declaration"}
   (js-keyword "export") (white-space) (js-keyword "default") (white-space) (render-node (get node "declaration"))])

(def demo ["export default a"])
