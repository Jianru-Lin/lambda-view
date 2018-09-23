;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-if
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; IfStatement
(defn render [node]
  (let [id (id-of node)
        test (get node "test")
        test-id (str id ".test")
        consequent (get node "consequent")
        alternate (get node "alternate")]
    [:div {:class "if statement"}
     (js-keyword "if") (white-space-optional) (smart-box {:id            test-id
                                                          :pair          :parenthesis
                                                          :init-collapse false} [test]) (white-space-optional)
     (render-node consequent)
     (if (nil? alternate)
       nil
       [:div
        (white-space-optional)
        (js-keyword "else")
        (white-space-optional)
        (render-node alternate)])]))

(def demo ["if (1) 2"
           "if (1) {2}"
           ;; "if (1) 2 else 3" NOT WORKING, Invalid Grammar
           "if (1) {2} else 3"
           "if (1) {2} else {3}"
           "if (1) {2} else if (3) {4}"
           "if (1) {2} else if (3) {4} else {5}"])
