;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-switch
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              colon
                                              white-space
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; SwitchStatement
(defn switch-statement-render [node]
  (let [id (id-of node)
        discriminant (get node "discriminant")
        cases (get node "cases")]
    [:div {:class "switch statement"}
     (js-keyword "switch")
     (white-space-optional)
     (smart-box {:id            id
                 :pair          :parenthesis
                 :init-collapse false} [discriminant])
     (white-space-optional)
     (smart-box {:id            (str id ".cases")
                 :pair          :brace
                 :init-collapse false
                 :init-layout   "vertical"} cases)]))

;; SwitchCase
(defn switch-case-render [node]
  (let [id (id-of node)
        consequent (get node "consequent")
        test (get node "test")]
    [:div {:class "switch-case"}
     [:div {:class "test"} (if (nil? test) [:div
                                            (js-keyword "default")
                                            (colon)]
                                           [:div
                                            (js-keyword "case")
                                            (white-space)
                                            (render-node test)
                                            (colon)])]
     (white-space-optional)
     [:div {:class "consequent"} (smart-box {:id            id
                                             :pair          :brace
                                             :init-collapse true} consequent)]]))


(def demo ["switch(a) {}"
           "switch(a) {default: 1}"
           "switch(a) {case \"a\": case \"b\": 1}"
           "switch(a) {case \"a\": 1; case \"b\": 2}"])
