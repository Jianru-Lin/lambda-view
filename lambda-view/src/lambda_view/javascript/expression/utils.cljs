(ns lambda-view.javascript.expression.utils
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box]]
        [lambda-view.tag :only [id-of]]))

(def priority-table {
                     "SequenceExpression"       0
                     "YieldExpression"          1
                     "AwaitExpression"          1
                     "AssignmentExpression"     1
                     "ConditionalExpression"    2
                     "ArrowFunctionExpression"  2
                     "BinaryExpression"         {"|"          5
                                                 "^"          6
                                                 "&"          7
                                                 "=="         8
                                                 "!="         8
                                                 "==="        8
                                                 "!=="        8
                                                 "<"          9
                                                 ">"          9
                                                 "<="         9
                                                 ">="         9
                                                 "in"         9
                                                 "instanceof" 9
                                                 "<<"         10
                                                 ">>"         10
                                                 ">>>"        10
                                                 "+"          11
                                                 "-"          11
                                                 "*"          12
                                                 ;; TODO "**"
                                                 "%"          12
                                                 "/"          12}
                     "LogicalExpression"        {"||" 3
                                                 "&&" 4}
                     "UnaryExpression"          13
                     "UpdateExpression"         14
                     "CallExpression"           15
                     "NewExpression"            16
                     "TaggedTemplateExpression" 17
                     "MemberExpression"         18
                     })

(defn priority-of [node]
  (let [p (get priority-table (get node "type"))]
    (cond
      ;; not found
      (nil? p)
      (throw (js/Error. (str "Priority Not Defined for: " (get node "type"))))

      ;; BinaryExpression
      (or
        (= (get node "type") "BinaryExpression")
        (= (get node "type") "LogicalExpression"))
      (let [op-p (get p (get node "operator"))]
        (println "BinaryExpression" (get node "operator") op-p)
        (if-not (nil? op-p) op-p
                            (throw (js/Error. (str "BinaryExpression Operator Priority Not Defined: " (get node "type"))))))

      ;; ok
      true
      p)))

(defn render-node-by-priority [current child]
  (println "render-node-by-priority" child)
  (let [child-type (get child "type")]
    (cond
      ;; basic element (Primary Expression)
      (or (= child-type "Identifier")
          (= child-type "Literal")
          (= child-type "TemplateLiteral"))
      (render-node child)

      ;; special cases (is this neccesarry/complete?)
      (or (= child-type "ObjectExpression")
          (= child-type "FunctionExpression")
          (= child-type "ArrowFunctionExpression"))
      (smart-box {:id            (id-of current)
                  :pair          :parenthesis
                  :init-collapse false
                  :style         :mini} [child])

      ;; sub expression element
      (< (priority-of child) (priority-of current))
      (do (println "child < current" (priority-of child) "<" (priority-of current))
          (smart-box {:id            (id-of current)
                      :pair          :parenthesis
                      :init-collapse false
                      :style         :mini} [child]))

      true
      (do (println "child > current" (priority-of child) ">" (priority-of current))
          (render-node child))
      )))