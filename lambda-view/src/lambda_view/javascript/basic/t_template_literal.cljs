;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.basic.t-template-literal
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.tag :only [id-of
                                mark-id!]]
        [lambda-view.javascript.common :only [smart-box]]))

;; TemplateLiteral
(defn template-literal-render [node]
  (let [id (id-of node)
        expressions (get node "expressions")
        quasis (get node "quasis")]
    [:div.template-literal (smart-box {:id            id
                                       :pair          :back-quote
                                       :seperator     :none
                                       :init-collapse false
                                       :auto-render   false} (doall (map-indexed (fn [idx e]
                                                                                   (mark-id! e)
                                                                                   (if (even? idx) (with-meta (render-node e) {:key (id-of e)})
                                                                                                   (if-not (nil? e) (with-meta (smart-box {:id            (str id ".quasis." idx)
                                                                                                                                           :pair          :$brace
                                                                                                                                           :init-collapse false} [e]) {:key (id-of e)}))))
                                                                                 (interleave quasis (conj expressions nil)))))]))

;; TemplateElement
(defn template-element-render [node]
  (let [id (id-of node)
        value (get node "value")
        raw-value (get value "raw")
        tail (get node "tail")]
    [:div.template-element raw-value]))

(def demo ["`hello`;"
           "`${name}`;"
           "`hello, ${name}`;"
           "`${name}, hello`;"
           "`${u}${v}${w}`;"])
