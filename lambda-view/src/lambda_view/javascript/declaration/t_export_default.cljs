;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-export-default
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space]]
        [lambda-view.tag :only [id-of]]))

;; ExportDefaultDeclaration
(defn render [node]
  [:div {:class "export-default declaration"}
   (js-keyword "export")
   (white-space)
   (js-keyword "default")
   (white-space)
   (render-node (get node "declaration"))])

(def demo [
           ;"export default a"
           ;"export default class s {}"
           ;"export default function f() {}"
           ;"export default function * f() {}"
           ;"export default async function f() {}"
           "export default async function * f() {}"
           ;"export default u = 100"
           ])
