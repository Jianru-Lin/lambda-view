;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.render
  (:require [lambda-view.javascript.bridge :as bridge])
  (:use [lambda-view.tag :only [mark-id!
                                id-of]]))

(defn node-render-not-found [node]
  [:div (str "Node render not found of type: " (get node "type"))])

(defn render-node [node]
  (if (nil? node) nil
                  (let [type (get node "type")
                        render (bridge/call-render-imp-of-type type)]
                    (if (nil? render) node-render-not-found
                                      (do (mark-id! node)
                                          (render node))))))

;(defn render-node-coll [nodes]
;  (doall (map render-node nodes)))
(defn render-node-coll [nodes]
  (doall (map (fn [e]
                (let [rendered-e (render-node e)
                      id (id-of e)]
                  (with-meta rendered-e {:key id})))
              nodes)))

(bridge/setup-render-node render-node)