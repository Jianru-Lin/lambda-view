(ns lambda-view.core
  (:require [reagent.core :as reagent :refer [atom]]
            [lambda-view.javascript :as js-lang]
            [lambda-view.javascript-demo :as js-demo]))

(enable-console-print!)

(println "This text is printed from src/lambda-view/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:source   (js-demo/current)
                          :ast-json nil
                          :ast      nil
                          :error    nil}))

(defn parse! [source]
  (try
    (let [ast-raw (js/acorn.parse source #js {:ecmaVersion                 "10"
                                              ;; :sourceType                  "module"
                                              :sourceType                  "script"
                                              :allowReturnOutsideFunction  true
                                              :allowImportExportEverywhere true
                                              :allowAwaitOutsideFunction   true
                                              :allowHashBang               true})
          ast-json (js/JSON.parse (js/JSON.stringify ast-raw))
          ast (js->clj ast-json)]
      (swap! app-state assoc :ast-json ast-json)
      (swap! app-state assoc :ast ast)
      (swap! app-state assoc :error nil))
    (catch :default e
      (swap! app-state assoc :ast nil)
      (swap! app-state assoc :error (.-message e)))))

(defn regen [source]
  (swap! app-state assoc :source source)
  (parse! source))

(defn src-editor []
  [:div
   [:h1 "Source Editor"]
   [:textarea {:style     {:display :block
                           :width   "100%"
                           :height  100}
               :value     (:source @app-state)
               :on-change (fn [e] (let [source (.-target.value e)] (regen source)))}]])

(defn ast-viewer []
  [:div
   [:h1 "AST"]
   (let [ast (:ast-json @app-state)
         error (:error @app-state)]
     (if (nil? error)
       [:pre (js/JSON.stringify ast nil 4)]
       [:div {:style {:color "red"}} error]))])

(defn ast-render []
  [:div
   [:h1 "AST-Render"]
   [:div.lambda-view [js-lang/ast-render (:ast @app-state)]]])

(defn hello-world []
  [:div
   [src-editor]
   [:div {:style {:display "grid"
                  :grid-template-columns "1fr 1fr"}}
    [ast-viewer]
    [ast-render]]])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(regen (:source @app-state))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (swap! app-state assoc :source (js-demo/current))
  (regen (:source @app-state)))
