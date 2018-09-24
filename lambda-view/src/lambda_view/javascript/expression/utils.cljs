(ns lambda-view.javascript.expression.utils
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box]]
        [lambda-view.tag :only [id-of
                                mark-id!]]))

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
        (if-not (nil? op-p) op-p
                            (throw (js/Error. (str "BinaryExpression Operator Priority Not Defined: " (get node "type"))))))

      ;; ok
      true
      p)))

(defn render-node-by-priority [current child]
  (let [child-type (get child "type")]
    (cond
      ;; basic element (Primary Expression)
      (or (= child-type "Identifier")
          (= child-type "Literal")
          (= child-type "ThisExpression")
          (= child-type "TemplateLiteral")
          (= child-type "ArrayExpression"))
      (render-node child)

      ;; special cases (Primary Expression too) (is this neccesarry/complete?)
      ;; NOT ALWAYS, eg:
      ;;     OK:      if ({a: 1}.a == 1) 2
      ;;     NOT OK:  {a: 1}.a
      (or (= child-type "ClassExpression")
          (= child-type "ObjectExpression")
          (= child-type "FunctionExpression")
          (= child-type "ArrowFunctionExpression")
          (= child-type "AwaitExpression")
          (= child-type "YieldExpression"))
      (smart-box {:id            (str (id-of child) ".b")
                  :pair          :parenthesis
                  :init-collapse false
                  :style         :mini} [child])

      ;; sub expression element
      (< (priority-of child) (priority-of current))
      (do #_(println "child < current" (priority-of child) "<" (priority-of current))
        (smart-box {:id            (str (id-of child) ".b")
                    :pair          :parenthesis
                    :init-collapse false
                    :style         :mini} [child]))

      true
      (do #_(println "child > current" (priority-of child) ">" (priority-of current))
        (render-node child))
      )))