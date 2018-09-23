;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-export-all
  (:use [lambda-view.javascript.render :only [render-node
                                              render-node-coll]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              semicolon
                                              asterisk]]
        [lambda-view.tag :only [id-of]]))

;; ExportDefaultDeclaration
(defn render [node]
  [:div.export-default.declaration
   (js-keyword "export")
   (white-space)
   (asterisk)
   (white-space)
   (js-keyword "from")
   (white-space)
   (render-node (get node "source"))
   (white-space-optional)
   (semicolon)])

(def demo ["export * from \"m\""])
