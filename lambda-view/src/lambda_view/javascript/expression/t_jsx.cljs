(ns lambda-view.javascript.expression.t-jsx
  (:use [lambda-view.javascript.render :only [render-node
                                              render-node-coll]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.common :only [white-space
                                              smart-box]]))

(defn jsx-element-render [node]
  (let [id (id-of node)
        open-e (get node "openingElement")
        closing-e (get node "closingElement")
        children (get node "children")]
    [:div.jsx-element.expression
     (render-node open-e)
     (if-not (nil? children) (smart-box {:id            id
                                         :pair          :none
                                         :seperator     :none
                                         :init-collapse false
                                         :init-layout   "vertical"} children))
     (if-not (nil? closing-e) (render-node closing-e))]))

(defn jsx-opening-element-render [node]
  (let [id (id-of node)
        attributes (get node "attributes")
        name (get node "name")
        self-closing (get node "selfClosing")]
    [:div.jsx-opening-element
     "<"
     (render-node name)
     (if (and (not (nil? attributes))
              (> (count attributes) 0)) [:div (white-space) (smart-box {:id            id
                                                                        :pair          :none
                                                                        :style         :mini
                                                                        :seperator     :white-space
                                                                        :init-collapse false
                                                                        :init-layout   "vertical"} attributes)])
     (if self-closing "/>" ">")]))

(defn jsx-attribute-render [node]
  (let [name (get node "name")
        value (get node "value")]
    (if (nil? value) [:div.jsx-attribute (render-node name)]
                     [:div.jsx-attribute (render-node name) "=" (render-node value)])))

(defn jsx-closing-element-render [node]
  (let [name (get node "name")]
    [:div.jsx-closing-element "</" (render-node name) ">"]))

(defn jsx-expression-container-render [node]
  (let [id (id-of node)
        expression (get node "expression")]
    (smart-box {:id   id
                :pair :brace} [expression])))

(defn jsx-identifier [node]
  (let [name (get node "name")]
    [:div.jsx-identifier name]))

(defn jsx-text [node]
  (let [raw (get node "raw")]
    [:div.jsx-text raw]))

(def demo [;"<a/>;"
           ;"<a></a>;"
           ;"<a attr=\"v\" attr2/>;"
           ;"<a t1={1}/>;"
           ;"<a t1={1} t2=\"2\"/>;"
           "<a>text</a>;"
           "<a><b/></a>;"
           ])