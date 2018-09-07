;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.render
  (:require [lambda-view.javascript.bridge :as bridge])
  (:use [lambda-view.tag :only [mark-id!
                                id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

(defn node-render-not-found [node]
  [:div (str "Node render not found of type: " (get node "type"))])

(defn render-node [node]
  (if (nil? node) nil
                  (let [type (get node "type")
                        render (bridge/call-render-imp-of-type type)]
                    (if (nil? render) node-render-not-found
                                      (do (mark-id! node)
                                          (render node))))))

(defn render-node-coll [nodes]
  (map render-node nodes))

(defn render-exp-node [exp-node parent-exp-node]
  ; TODO Compare Priority...
  (mark-id! exp-node)
  (if (or (nil? parent-exp-node)
          (= "Identifier" (get exp-node "type"))) (render-node exp-node)
                                                  (let [id (id-of exp-node)]
                                                    (println "id" id)
                                                    (init-collapse! id false)
                                                    (init-layout! id "horizontal")
                                                    #_(collapsable-box {:id    id
                                                                        :style :mini} (render-node exp-node))
                                                    (render-node exp-node))))

(bridge/setup-render-node render-node)