;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-return
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              semicolon
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ReturnStatement
(defn render [node]
  (let [argument (get node "argument")]
    (if (nil? argument)
      [:div {:class "return statement"} (js-keyword "return") (white-space-optional) (semicolon)]
      [:div {:class "return statement"} (js-keyword "return") (white-space) (render-node argument) (white-space-optional) (semicolon)])))

(def demo ["return"
            "return 1"])
