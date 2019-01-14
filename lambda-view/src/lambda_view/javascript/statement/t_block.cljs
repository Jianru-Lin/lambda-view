;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-block
  (:use [lambda-view.javascript.common :only [smart-box]]
        [lambda-view.tag :only [id-of]]))

;; BlockStatement
(defn render [node]
  (let [id (id-of node)
        body (get node "body")]
    [:div.block.statement
     (smart-box {:id            id
                 :pair          :brace
                 :init-collapse true
                 :init-layout   "vertical"} body)]))


(def demo [
           ;"{}"
           "{1}"
           ;"{label: statement}"
           ])
