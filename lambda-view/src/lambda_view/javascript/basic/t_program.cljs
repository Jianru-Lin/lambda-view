;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.basic.t-program
  (:use [lambda-view.javascript.render :only [render-node-coll]]))

;; Program
(defn render [node]
  [:div
   {:class "program"}
   (let [body (get node "body")]
     (if-not (nil? body) (render-node-coll body)))])

(def demo [])