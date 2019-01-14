(ns lambda-view.core
  (:require [reagent.core :as reagent :refer [atom]]
            [lambda-view.javascript.core :as js-lang]
            [lambda-view.playground :as playground]))

(enable-console-print!)

(defn ^:export render [container ast]
  (reagent/render-component [:div.lambda-view [js-lang/ast-render (js->clj ast)]]
                            container))

(defn ^:export playground []
  (playground/init))

(println "lambda-view is ready")