;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.basic.t-identifier
  (:use [lambda-view.tag :only [id-of]]))

;; Identifier
(defn render [node]
  [:div {:class "identifier"} (get node "name")])

(def demo ["a"])