(ns lambda-view.state
  (:require [reagent.core :as reagent]))

;; Collapse State ;; TODO GC

;; id -> boolean
(def collapse (reagent/atom {}))

(defn init-collapse! [id v]
  (let [current (get @collapse id)]
    (if (nil? current) (do (println "init-collapse!" id v)
                           (swap! collapse assoc id v)))))

(defn get-collapse [id]
  (let [v (get @collapse id)
        ret (if (nil? v) false v)]
    (println "get-collapse" id ret)
    ret))

(defn set-collapse! [id v]
  (println "set-collapse!" id v)
  (swap! collapse assoc id v))

(defn toggle-collapse! [id]
  (let [v (get @collapse id)]
    (if-not (nil? v) (do (println "toggle-collapse!" id "from" v "to" (not v))
                         (set-collapse! id (not v)))
                     (do (println "toggle-collapse!" id "ignore")))))

;; Layout State ;; TODO GC

;; id -> "horizontal" | "vertical"
(def layout (reagent/atom {}))

(defn init-layout! [id v]
  (let [current (get @layout id)]
    (if (nil? current) (do (println "init-layout!" id v)
                           (swap! layout assoc id v)))))

(defn get-layout [id]
  (let [v (get @layout id)
        ret (if (nil? v) false v)]
    (println "get-layout" id ret)
    ret))

(defn set-layout! [id v]
  (println "set-layout!" id v)
  (swap! layout assoc id v))

(defn toggle-layout! [id]
  (let [v (get @layout id)]
    (if-not (nil? v) (let [new-v (if (= "horizontal" v) "vertical" "horizontal")]
                       (println "toggle-layout!" id "from" v "to" new-v)
                       (swap! layout assoc id new-v))
                     (do (println "toggle-layout!" id "ignore")))))

;; Hover State ;; TODO GC

;; id -> boolean
(def hover (reagent/atom {}))

(defn init-hover! [id v]
  (let [current (get @hover id)]
    (if (nil? current) (do (println "init-hover!" id v)
                           (swap! hover assoc id v)))))

(defn get-hover [id]
  (let [v (get @hover id)
        ret (if (nil? v) false v)]
    (println "get-hover" id ret)
    ret))

(defn set-hover! [id v]
  (println "set-hover!" id v)
  (swap! hover assoc id v))

(defn toggle-hover! [id]
  (let [v (get @hover id)]
    (if-not (nil? v) (do (println "toggle-hover!" id "from" v "to" (not v))
                         (set-hover! id (not v)))
                     (do (println "toggle-hover!" id "ignore")))))