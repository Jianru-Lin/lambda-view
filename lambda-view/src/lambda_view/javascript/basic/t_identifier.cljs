;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.basic.t-identifier)

;; Identifier
(defn render [node]
  [:div {:class "identifier"} (get node "name")])

(def demo ["a"])