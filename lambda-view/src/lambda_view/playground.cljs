(ns lambda-view.playground
  (:require-macros [cljs.core.async :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [lambda-view.javascript.core :as js-lang]
            [lambda-view.state :as js-state]
            [cljs.core.async :refer [<!]]
            [cljs-http.client :as http]))

; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:source                 (js-lang/current)
                          :compile-on-change      true
                          :validate-after-compile false
                          :show-ast               false
                          :ast-json               nil
                          :ast                    nil
                          :error                  nil
                          :loading                false}))

(defn parse-config []
  #js {:ecmaVersion                 "10"
       :sourceType                  "module"
       ;:sourceType                  "script"
       :allowReturnOutsideFunction  true
       :allowImportExportEverywhere true
       :allowAwaitOutsideFunction   true
       :allowHashBang               true})

(defn parse! [source]
  (try
    (let [ast-raw (js/parse source (parse-config))
          ast-json (js/JSON.parse (js/JSON.stringify ast-raw))
          ast (js->clj ast-json)]
      (swap! app-state assoc :ast-json ast-json)
      (swap! app-state assoc :ast ast)
      (swap! app-state assoc :error nil))
    (catch :default e
      (swap! app-state assoc :ast nil)
      (swap! app-state assoc :error (.-message e)))))

(defn validate []
  (let [source (.-textContent (js/document.querySelector ".lambda-view"))]
    (try
      (do (js/parse source (parse-config))
          (js/console.log "Validate OK"))
      (catch :default e
        (js/console.error e)
        (js/console.log source)
        (js/alert "Validate fail")))))

(defn regen [source]
  (swap! app-state assoc :source source)
  (parse! source)
  (if (:validate-after-compile @app-state) (validate)))

(defn load-remote-source [url]
  (swap! app-state assoc :loading true)
  (go (let [response (<! (http/get url))]
        (swap! app-state assoc :loading false)
        (if (= 200 (:status response))
          (if (= true (:compile-on-change @app-state)) (regen (:body response))
                                                       (swap! app-state assoc :source (:body response)))))))

(defn src-editor []
  [:div
   [:h1 "Source Editor"]
   [:textarea {:style     {:display     :block
                           :box-sizing  "border-box"
                           :width       "100%"
                           :font-size   "inherit"
                           :font-family "inherit"}
               :disabled  (:loading @app-state)
               :rows      "8"
               :value     (:source @app-state)
               :on-change (fn [e]
                            (let [source (.-target.value e)]
                              (if (:compile-on-change @app-state) (regen source)
                                                                  (swap! app-state assoc :source source))))}]
   [:div {:style {:display        :flex
                  :flex-direction :rows
                  :padding        "16px 0"}}
    [:label [:input {:type      "checkbox"
                     :checked   (:show-ast @app-state)
                     :on-change (fn [e]
                                  (swap! app-state assoc :show-ast (.-checked (.-target e))))}] "Show AST"]
    [:label [:input {:type      "checkbox"
                     :style     {:margin-left 16}
                     :checked   (:compile-on-change @app-state)
                     :on-change (fn [e]
                                  (swap! app-state assoc :compile-on-change (.-checked (.-target e))))}] "Compile On Change"]
    [:label [:input {:type      "checkbox"
                     :style     {:margin-left 16}
                     :checked   (:validate-after-compile @app-state)
                     :on-change (fn [e]
                                  (swap! app-state assoc :validate-after-compile (.-checked (.-target e))))}] "Validate After Compile"]
    [:label [:input {:type      "checkbox"
                     :style     {:margin-left 16}
                     :checked   (= true (js-state/get-force-collapse))
                     :on-change (fn [e]
                                  (let [v (.-checked (.-target e))]
                                    (if v (js-state/set-force-collapse true)
                                          (js-state/set-force-collapse nil))))}] "Force Collapse"]
    [:button {:disabled (:compile-on-change @app-state)
              :style    {:margin-left 16}
              :on-click (fn [] (regen (:source @app-state)))} "Compile"]
    [:button {:disabled (:loading @app-state)
              :style    {:margin-left 16}
              :on-click (fn [] (load-remote-source "/data/jquery.js"))} "Load jQuery"]
    [:button {:disabled (:loading @app-state)
              :style    {:margin-left 16}
              :on-click (fn [] (regen ""))} "Clear"]
    [:button {:disabled (:loading @app-state)
              :style    {:margin-left 16}
              :on-click (fn [] (validate))} "Validate"]]])

(defn ast-render []
  [:div
   [:h1 "AST Render"]
   [:div.lambda-view [js-lang/ast-render (:ast @app-state)]]])

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
        jsx (filter (fn [[type, _]] (not (nil? (re-find #"^JSX" type))))
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
      (doall (map render-item exprs))]
     [:h3 "JSX"]
     [:ul
      (doall (map render-item jsx))]]))

(defn ast-viewer []
  [:div
   [:h1 "AST"]
   (let [ast (:ast-json @app-state)
         error (:error @app-state)]
     (if (nil? error)
       [:pre (js/JSON.stringify ast nil 4)]
       [:div {:style {:color "red"}} error]))])


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
     (if (:show-ast @app-state) [ast-viewer])
     [ast-render]]]])

(defn init []
  (reagent/render-component [hello-world]
                            (. js/document (getElementById "app")))
  (regen (:source @app-state)))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (swap! app-state assoc :source (js-lang/current))
  (regen (:source @app-state)))
