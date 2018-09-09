;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-for-in
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              semicolon
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ForInStatement
(defn render [node]
  (let [id (id-of node)
        left (get node "left")
        right (get node "right")
        body (get node "body")]
    [:div {:class "for-in statement"}
     (js-keyword "for")
     (white-space-optional)
     (smart-box {:id            id
                 :pair          :parenthesis
                 :seperator     :none
                 :init-collapse false
                 :init-layout   "horizontal"
                 :auto-render   false} (list (render-node left)
                                             (white-space) (js-keyword "in") (white-space)
                                             (render-node right)))
     (white-space-optional)
     (render-node body)]))

(def demo ["for(a in b) 1"
           "for(a in b) {1}"
           ;; TODO "for await (const x in xs) {}"
           ])
