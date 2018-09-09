;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-for
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              semicolon
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ForStatement
(defn render [node]
  (let [id (id-of node)
        init (get node "init")
        test (get node "test")
        update (get node "update")
        body (get node "body")]
    [:div {:class "for statement"}
     (js-keyword "for")
     (white-space-optional)
     (smart-box {:id            id
                 :pair          :parenthesis
                 :seperator     :semicolon
                 :init-collapse false
                 :init-layout "horizontal"} [init test update])
     (white-space-optional)
     (render-node body)]))


(def demo ["for(;;) 1"
           "for(u;;) 1"
           "for(;v;) 1"
           "for(;;w) 1"
           "for(a;b;c) 1"
           "for (a;b;c) {1}"])