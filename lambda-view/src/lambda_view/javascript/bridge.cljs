(ns lambda-view.javascript.bridge)

(defn setup-render-node [f]
  #_(println "setup-render-node" f)
  (set! (.-lv_render_node js/window) f))

(defn call-render-node [node]
  (let [f (.-lv_render_node js/window)
        v (f node)]
    #_(println "call-render-node" node "=>" v)
    v))

(defn setup-render-imp-of-type [f]
  #_(println "setup-render-imp-of-type" f)
  (set! (.-lv_render_imp_of_type js/window) f))

(defn call-render-imp-of-type [type]
  (let [f (.-lv_render_imp_of_type js/window)
        v (f type)]
    #_(println "call-render-imp-of-type" type "=>" v)
    v))