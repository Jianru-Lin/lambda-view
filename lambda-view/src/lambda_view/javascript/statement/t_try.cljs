;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-try
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; TryStatement
(defn try-statement-render [node]
  (let [block (get node "block")
        handler (get node "handler")
        finalizer (get node "finalizer")]
    (let [basic [:div {:class "try statement"}
                 (js-keyword "try") (white-space-optional) (render-node block)]
          with-handler (fn [target] (if-not (nil? handler) (conj target
                                                                 (white-space-optional)
                                                                 (render-node handler))
                                                           target))
          with-finalizer (fn [target] (if-not (nil? target) (conj target
                                                                  (with-meta (white-space-optional) {:key ""})
                                                                  (js-keyword "finally")
                                                                  (white-space-optional)
                                                                  (render-node finalizer))
                                                            target))]
      (with-finalizer (with-handler basic)))))

;; CatchClause
(defn catch-clause-render [node]
  (let [id (id-of node)
        param (get node "param")
        body (get node "body")]
    [:div {:class "catch-clause"}
     (js-keyword "catch")
     (white-space-optional)
     (if-not (nil? param) (smart-box {:id            (str id ".param")
                                      :pair          :parenthesis
                                      :init-collapse false
                                      :auto-render   false} [(render-node param)]))
     (if-not (nil? param) (white-space-optional))
     (render-node body)]))


(def demo ["try {a} catch {b}"
           "try {a} finally {b}"
           "try {a} catch {b} finally {c}"
           "try {a} catch(e) {b} finally {c}"])
