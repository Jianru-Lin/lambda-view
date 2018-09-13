;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-export-named
  (:use [lambda-view.javascript.render :only [render-node
                                              render-node-coll]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ExportNamedDeclaration
(defn export-named-render [node]
  (let [id (id-of node)
        declaration (get node "declaration")
        specifiers (get node "specifiers")
        source (get node "source")]
    [:div.export-named.declaration
     (js-keyword "export")
     (white-space)
     (cond
       ;; export function f() {}
       (not (nil? declaration))
       (render-node declaration)

       ;; export {a, b}
       ;; export {a, b} from "m"
       true
       (smart-box {:id            id
                   :pair          :brace
                   :init-collapse false
                   :seperator     :comma
                   :init-layout   (if (> (count specifiers) 4) "vertical" "horizontal")} specifiers))
     (if-not (nil? source) [:div
                            (white-space)
                            (js-keyword "from")
                            (white-space)
                            (render-node source)])]))

;; ExportSpecifier
(defn export-specifier-render [node]
  (let [local (get node "local")
        exported (get node "exported")]
    (if (= (get local "name")
           (get exported "name")) [:div.export-specifier
                                   (render-node local)]
                                  [:div.export-specifier
                                   (render-node local)
                                   (white-space)
                                   (js-keyword "as")
                                   (white-space)
                                   (render-node exported)])))

(def demo ["export function f() {}"
           "export {a1}"
           "export {a2,b2 as b3}"
           "export {a3} from \"m1\""
           "export var x = 100"])
