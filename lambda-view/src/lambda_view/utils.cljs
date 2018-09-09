(ns lambda-view.utils
  (:require [clojure.test :refer [deftest is]]))

;; WARN coll must be list, not vector
(defn join [coll el]
  (println "input" coll)
  (println "input type" (type coll))
  (cond
    ;; nil is ok
    (= coll nil) nil
    ;; seq is ok
    (seq? coll) (let [tail-idx (- (count coll) 1)
                      temp-coll (doall (map-indexed (fn [idx item] [idx item el]) coll))
                      reduce-op (fn [left item] (let [[idx item el] item]
                                                  (if (= idx tail-idx) (conj left item)
                                                                       (conj (conj left item) el))))
                      final-coll (reverse (reduce reduce-op (concat '(()) temp-coll)))]
                  (println "output" final-coll)
                  final-coll)
    ;; others is not ok
    true (throw (js/Error. (str "Not support: " (type coll))))))

(deftest test-join
  (is (= nil (join nil ",")))
  (is (= [] (join [] ",")))
  (is (= ["a"] (join ["a"] ",")))
  (is (= ["a" "," "b"] (join ["a" "b"] ",")))
  (is (= ["a" "," "b" "," "c"] (join ["a" "b" "c"] ",")))
  (is (= () (join () ",")))
  (is (= '("a") (join '("a") ",")))
  (is (= '("a" "," "b") (join '("a" "b") ",")))
  (is (= '("a" "," "b" "," "c") (join '("a" "b" "c") ",")))
  (is (= '([:div] "," [:div]) (join '([:div] [:div]) ","))))

