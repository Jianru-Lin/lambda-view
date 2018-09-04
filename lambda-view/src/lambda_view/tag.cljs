(ns lambda-view.tag)

(defn gen-id-for [_node]
  ;; TODO poor algorithm :(
  (str "r_" (.substring (.toString (js/Math.random)) 2)))

(defn has-id [node]
  (and (not (nil? node))
       (.hasOwnProperty node "lv_id")))

(defn id-of [node]
  (if (nil? node) nil
                  (.-lv_id node)))
(defn mark-id! [node]
  (if-not (nil? node) (if (has-id node) (id-of node)
                                        (let [id (gen-id-for node)]
                                          (set! (.-lv_id node) id)
                                          id))))
