(ns lambda-view.core
  (:require [reagent.core :as reagent :refer [atom]]
            [lambda-view.javascript.core :as js-lang]))

(enable-console-print!)

(println "This text is printed from src/lambda-view/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:source   (js-lang/current)
                          :ast-json nil
                          :ast      nil
                          :error    nil}))

(defn parse! [source]
  (try
    (let [ast-raw (js/acorn.parse source #js {:ecmaVersion                 "10"
                                              :sourceType                  "module"
                                              ;:sourceType                  "script"
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
   [:textarea {:style     {:display     :block
                           :box-sizing  "border-box"
                           :width       "100%"
                           :font-size   "inherit"
                           :font-family "inherit"}
               :rows      "8"
               :value     (:source @app-state)
               :on-change (fn [e] (let [source (.-target.value e)] (regen source)))}]])

;; Demo List
(defn demo-list []
  (let [demos (doall (map (fn [[type, demo]] [type, (clojure.string/join "\n" demo)])
                          js-lang/type-demo))
        basic (filter (fn [[type, _]] (and (nil? (re-find #"Declaration$" type))
                                           (nil? (re-find #"Statement$" type))
                                           (nil? (re-find #"Expression$" type))))
                      demos)
        decls (filter (fn [[type, _]] (not (nil? (re-find #"Declaration$" type))))
                      demos)
        stams (filter (fn [[type, _]] (not (nil? (re-find #"Statement$" type))))
                      demos)
        exprs (filter (fn [[type, _]] (not (nil? (re-find #"Expression$" type))))
                      demos)
        render-item (fn [[type, demo]]
                      ^{:key type} [:li
                                    {:on-click (fn [] (regen demo))}
                                    [:a {:href "javascript:"}
                                     type]])]
    [:div.demo-list {:style {:overflow "auto"}}
     [:h3 "Basic"]
     [:ul
      (doall (map render-item basic))]
     [:h3 "Declaration"]
     [:ul
      (doall (map render-item decls))]
     [:h3 "Statement"]
     [:ul
      (doall (map render-item stams))]
     [:h3 "Expression"]
     [:ul
      (doall (map render-item exprs))]]))

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
  [:div {:style {:display               "grid"
                 :grid-template-columns "auto 1fr"
                 :grid-gap              "16px"
                 :padding               "0 16px"
                 :position              "absolute"
                 :left                  0
                 :top                   0
                 :right                 0
                 :bottom                0}}
   [demo-list]
   [:div {:style {:overflow "auto"}}
    [src-editor]
    [:div {:style {:display               "grid"
                   :grid-template-columns "1fr 1fr"}}
     [ast-viewer]
     [ast-render]]]])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(regen (:source @app-state))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (swap! app-state assoc :source (js-lang/current))
  (regen (:source @app-state)))
