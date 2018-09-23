;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-variable
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              asterisk
                                              comma
                                              semicolon
                                              equal
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; VariableDeclaration
(defn variable-declaration-render [node]
  (let [kind (get node "kind")
        declarations (get node "declarations")]
    [:div {:class "variable declaration"}
     (js-keyword kind)
     (white-space)
     (smart-box {:id          (id-of node)
                 :pair        :none
                 :style       :mini
                 :seperator   :comma
                 :init-layout (if (> (count declarations) 4) "vertical" "horizontal")} declarations)
     (white-space-optional)
     (semicolon)]))

;; VariableDeclarator
(defn variable-declarator-render [node]
  (let [id (get node "id")
        init (get node "init")]
    (if (nil? init) [:div {:class "variable-declarator"}
                     (render-node id)]
                    [:div {:class "variable-declarator"}
                     (render-node id)
                     (white-space-optional)
                     (equal)
                     (white-space-optional)
                     (render-node init)])))


(def demo ["var a1"
           "var a2 = 1"
           "var a3 = 1, a4 = 2"
           "let b1"
           "let b2 = 1"
           "let b3 = 1, b4 = 2"
           ;; "const x" is invalid
           "const c1 = 1"
           "const c2 = 1, c3 = 2"])
