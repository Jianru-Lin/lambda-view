;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript
  (:require [lambda-view.utils :as utils]
            [reagent.core :as reagent])
  (:use [lambda-view.common :only [js-keyword
                                   white-space
                                   comma
                                   colon
                                   equal
                                   semicolon
                                   asterisk
                                   white-space-optional
                                   pair
                                   brackets
                                   parenthese
                                   braces
                                   box
                                   collapsed
                                   collapsable-box
                                   toggle-collapse]]
        [lambda-view.tag :only [mark-id!
                                id-of]]
        [lambda-view.state :only [init-collapse!
                                  get-collapse
                                  set-collapse!
                                  init-layout!
                                  get-layout
                                  set-layout!]]))

(declare type-render)

(defn node-render-not-found [node]
  [:div (str "Node render not found of type: " (get node "type"))])

(defn node-is-nil []
  ;[:div "Node is nil"]
  nil)

(defn render-for-node [node]
  (if (nil? node) node-is-nil (let [type (get node "type")
                                    render (get type-render type)]
                                (if (nil? render) node-render-not-found render))))

(defn render-node [node]
  (mark-id! node)
  [(render-for-node node) node])

(defn render-node-coll [nodes]
  (map render-node nodes))

;; EmptyStatement
(defn empty-statement-render [_node]
  [:div {:class "empty statement"} ";"])

;; DebuggerStatement
(defn debugger-statement-render [_node]
  [:div {:class "debugger statement"} "debugger"])

;; ReturnStatement
(defn return-statement-render [node]
  (let [argument (get node "argument")]
    (if (nil? argument)
      [:div {:class "return statement"} (js-keyword "return")]
      [:div {:class "return statement"} (js-keyword "return") (white-space) (render-node argument)])))

;; BlockStatement
(defn block-statement-render [node]
  (let [id (id-of node)
        body (get node "body")]
    (init-collapse! id true)
    [:div.block.statement
     [collapsable-box {:id   id
                       :pair :brace} (render-node-coll body)]]))

;; BreakStatement
(defn break-statement-render [_node]
  [:div {:class "break statement"} "break"])

;; ContinueStatement
(defn continue-statement-render [_node]
  [:div {:class "continue statement"} "continue"])

;; LabeledStatement
(defn labeled-statement-render [node]
  (let [label (get node "label")
        body (get node "body")]
    [:div {:class "labeled statement"}
     [:div {:class "label"} (render-node label)]
     (colon)
     (white-space-optional)
     [:div {:class "body"} (render-node body)]]))

;; ImportDeclaration
(defn import-declaration-render [node]
  (let [specifiers (get node "specifiers")
        source (get node "source")]
    [:div {:class "import declaration"}
     (js-keyword "import")
     (if (> (count specifiers) 0) (list (white-space)
                                        ;; see reference https://www.ecma-international.org/ecma-262/9.0/index.html#prod-ImportClause
                                        ;; there are 5 possible scenarios
                                        ;; 1. [ImportDefaultSpecifier]                              (import a from "module")
                                        ;; 2. [ImportNamespaceSpecifier]                            (import * as a from "module")
                                        ;; 3. [ImportSpecifier...]                                  (import { a } from "moodule")
                                        ;; 4. [ImportDefaultSpecifier, ImportNamespaceSpecifier]    (import a, * as b from "module")
                                        ;; 5. [ImportDefaultSpecifier, ImportSpecifier...]          (import a, { b } from "module")
                                        (let [first-sp (first specifiers)
                                              first-sp-type (get first-sp "type")
                                              first-sp-only (= 1 (count specifiers))
                                              second-sp (second specifiers)
                                              second-sp-type (get second-sp "type")
                                              rest-sps (rest specifiers)
                                              render-list (fn [import-specifier-list] [braces {} (list (white-space-optional)
                                                                                                       (utils/join (map #(render-node %1) import-specifier-list) (list (comma) (white-space-optional)))
                                                                                                       (white-space-optional))])]
                                          (cond
                                            ;; case 1
                                            (and first-sp-only
                                                 (= first-sp-type "ImportDefaultSpecifier")) (render-node first-sp)
                                            ;; case 2
                                            (and first-sp-only
                                                 (= first-sp-type "ImportNamespaceSpecifier")) (render-node first-sp)
                                            ;; case 3
                                            (and (not first-sp-only)
                                                 (= first-sp-type "ImportSpecifier")) (render-list specifiers)
                                            ;; case 4
                                            (and (= (count specifiers))
                                                 (= first-sp-type "ImportDefaultSpecifier")
                                                 (= second-sp-type "ImportNamespaceSpecifier")) (list (render-node first-sp)
                                                                                                      (comma)
                                                                                                      (white-space-optional)
                                                                                                      (render-node second-sp))
                                            ;; case 5
                                            (and (> (count specifiers) 1)
                                                 (= first-sp-type "ImportDefaultSpecifier")
                                                 (every? #(= (get %1 "type") "ImportSpecifier") rest-sps)) (list (render-node first-sp)
                                                                                                                 (comma)
                                                                                                                 (white-space-optional)
                                                                                                                 (render-list rest-sps))
                                            ;; UNKNOWN
                                            true (str "Unhandled case: " specifiers)))
                                        (white-space)
                                        (js-keyword "from")))
     (white-space)
     (render-node source)]))

;; ImportDefaultSpecifier
(defn import-default-specifier-render [node]
  [:div {:class "import-default-specifier"}
   (render-node (get node "local"))])

;; ImportSpecifier
(defn import-specifier-render [node]
  [:div {:class "import-specifier"}
   (let [imported (get node "imported")
         local (get node "local")]
     (if (= imported local) (render-node imported)
                            (list
                              (render-node imported)
                              (white-space)
                              (js-keyword "as")
                              (white-space)
                              (render-node local))))])

;; ImportNamespaceSpecifier
(defn import-namespace-specifier-render [node]
  [:div {:class "import-namespace-specifier"}
   "*" (white-space) (js-keyword "as") (white-space) (render-node (get node "local"))])

;; ExportDefaultDeclaration
(defn export-default-declaration-render [node]
  [:div {:class "export-default declaration"}
   (js-keyword "export") (white-space) (js-keyword "default") (white-space) (render-node (get node "declaration"))])

;; ExpressionStatement
(defn expression-statement-render [node]
  (let [expression (get node "expression")
        directive (get node "directive")]
    [:div {:class "expression statement"} (render-node expression)]))

;; ThrowStatement
(defn throw-statement-render [node]
  (let [argument (get node "argument")]
    [:div {:class "throw statement"}
     (js-keyword "throw") (white-space) (render-node argument)]))

;; WhileStatement
(defn while-statement-render [node]
  (let [test (get node "test")
        body (get node "body")]
    [:div {:class "while statement"}
     (js-keyword "while") (white-space-optional) "(" (render-node test) ")" (white-space-optional) ((render-for-node body) body)]))

;; DoWhileStatement
(defn do-while-statement-render [node]
  (let [body (get node "body")
        test (get node "test")]
    [:div {:class "do-while statement"}
     (js-keyword "do") (white-space-optional) (render-node body) (white-space-optional) (js-keyword "while") (white-space-optional) "(" (render-node test) ")"]))

;; IfStatement
(defn if-statement-render [node]
  (let [test (get node "test")
        consequent (get node "consequent")
        alternate (get node "alternate")]
    [:div {:class "if statement"}
     (js-keyword "if") (white-space-optional) "(" (render-node test) ")" (white-space-optional)
     (render-node consequent)
     (if (nil? alternate)
       nil
       (list (white-space-optional) (js-keyword "else") (white-space-optional) (render-node alternate)))]))

;; TryStatement
(defn try-statement-render [node]
  (let [block (get node "block")
        handler (get node "handler")
        finalizer (get node "finalizer")]
    [:div {:class "try statement"}
     (js-keyword "try") (white-space-optional) (render-node block)
     (if-not (nil? handler) (list (white-space-optional) (render-node handler)))
     (if-not (nil? finalizer) (list (white-space-optional) (js-keyword "finally") (white-space-optional) (render-node finalizer)))]))

;; CatchClause
(defn catch-clause-render [node]
  (let [param (get node "param")
        body (get node "body")]
    [:div {:class "catch-clause"}
     (js-keyword "catch")
     (white-space-optional)
     (if-not (nil? param) (list (parenthese (render-node param)) (white-space-optional)))
     (render-node body)]))

;; WithStatement
(defn with-statement-render [node]
  (let [object (get node "object")
        body (get node "body")]
    [:div {:class "with statement"}
     (js-keyword "with")
     (white-space-optional)
     (parenthese (render-node object))
     (white-space-optional)
     (render-node body)]))

;; VariableDeclaration
(defn variable-declaration-render [node]
  (let [kind (get node "kind")
        declarations (get node "declarations")]
    [:div {:class "variable declaration"}
     (js-keyword kind)
     (white-space)
     (utils/join (render-node-coll declarations) (list (comma) (white-space-optional)))]))

;; VariableDeclarator
(defn variable-declarator-render [node]
  (let [id (get node "id")
        init (get node "init")]
    [:div {:class "variable-declarator"}
     (render-node id)
     (if-not (nil? init) (list (white-space-optional)
                               (equal)
                               (white-space-optional)
                               (render-node init)))]))

;; ForStatement
(defn for-statement-render [node]
  (let [init (get node "init")
        test (get node "test")
        update (get node "update")
        body (get node "body")]
    [:div {:class "for statement"}
     (js-keyword "for")
     (white-space-optional)
     (parenthese (list (render-node init)
                       (semicolon) (white-space-optional)
                       (render-node test)
                       (semicolon) (white-space-optional)
                       (render-node update)))
     (white-space-optional)
     (render-node body)]))

;; ForOfStatement
(defn for-of-statement-render [node]
  (let [left (get node "left")
        right (get node "right")
        body (get node "body")]
    [:div {:class "for-of statement"}
     (js-keyword "for")
     (white-space-optional)
     (parenthese (list (render-node left)
                       (white-space) (js-keyword "of") (white-space)
                       (render-node right)))
     (white-space-optional)
     (render-node body)]))

;; FunctionDeclaration
(defn function-declaration-render [node]
  (let [generator (get node "generator")
        ;; [NOTE]
        ;; expression flag is such an old thing which is not supported anymore
        ;; check here: https://github.com/estree/estree/blob/master/deprecated.md#functions
        ;;             https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Expression_Closures
        ;; expression (get node "expression")
        async (get node "async")
        id (get node "id")
        params (get node "params")
        body (get node "body")]
    [:div {:class "function declaration"}
     (if async (list (js-keyword "async")
                     (white-space)))
     (js-keyword "function")
     (white-space)
     (if generator (list (asterisk)
                         (white-space)))
     (if-not (nil? id) (list (render-node id)
                             (white-space-optional)))
     (parenthese (utils/join (render-node-coll params) (list (comma) (white-space-optional))))
     (white-space-optional)
     (render-node body)]))

;; ClassDeclaration
(defn class-declaration-render [node]
  (let [id (get node "id")
        super-class (get node "superClass")
        body (get node "body")]
    [:div {:class "class declaration"}
     (js-keyword "class")
     (white-space)
     (render-node id)
     (if-not (nil? super-class) (list (white-space)
                                      (js-keyword "extends")
                                      (white-space)
                                      (render-node super-class)
                                      (white-space-optional)))
     (white-space-optional)
     (render-node body)]))

;; ClassBody
(defn class-body-render [node]
  (let [body (get node "body")]
    [:div {:class "class-body"}
     [braces {} [:div (render-node-coll body)]]]))

;; MethodDefinition
(defn method-definition-render [node]
  (let [kind (get node "kind")
        static (get node "static")
        computed (get node "computed")
        key (get node "key")
        value (get node "value")]
    [:div {:class "method-definition"}
     (if static (list (js-keyword "static") (white-space)))
     (cond
       ;; Constructor or Method
       (or (= kind "method")
           (= kind "constructor")) (let [fn-exp-node value
                                         _id (get fn-exp-node "id") ; always nil
                                         generator (get fn-exp-node "generator")
                                         _expression (get fn-exp-node "expression") ;; useless
                                         async (get fn-exp-node "async")
                                         params (get fn-exp-node "params")
                                         body (get fn-exp-node "body")]
                                     (list (if async (list (js-keyword "async") ;; optional?
                                                           (white-space)))
                                           (if generator (list (asterisk)
                                                               (white-space))) ;; optional?
                                           (render-node key)
                                           (white-space-optional)
                                           (parenthese (utils/join (render-node-coll params) (list (comma) (white-space-optional))))
                                           (white-space-optional)
                                           (render-node body))))]))

;; SwitchStatement
(defn switch-statement-render [node]
  (let [discriminant (get node "discriminant")
        cases (get node "cases")]
    [:div {:class "switch statement"}
     (js-keyword "switch")
     (white-space-optional)
     (parenthese (render-node discriminant))
     (white-space-optional)
     (block-statement-render {"body" cases})]))

;; SwitchCase
(defn switch-case-render [node]
  (let [consequent (get node "consequent")
        test (get node "test")]
    [:div {:class "switch-case"}
     [:div {:class "test"} (if (nil? test) (list (js-keyword "default")
                                                 (colon))
                                           (list (js-keyword "case")
                                                 (white-space)
                                                 (render-node test)
                                                 (colon)))]
     [:div {:class "consequent"} (render-node-coll consequent)]]))

;; Identifier
(defn identifier-render [node]
  [:div {:class "identifier"} (get node "name")])

;; Literal
(defn literal-render [node]
  [:div {:class "literal"} (get node "raw")])

;; Program
(defn program-render [node]
  [:div
   {:class "program"}
   (let [body (get node "body")]
     (if-not (nil? body) (render-node-coll body)))])

;; ArrayExpression
(defn array-expression-render [node]
  (let [state (reagent/atom {:layout   "horizontal"
                             :collapse true})
        toggle-layout (fn [] (swap! state assoc :layout (if (= "horizontal" (:layout @state)) "vertical" "horizontal")))
        toggle-collapse (fn [] (swap! state assoc :collapse (not (:collapse @state))))]
    (fn []
      (let [elements (get node "elements")
            tail-idx (- (count elements) 1)]
        [:div.array.expression
         (if (= 0 (count elements)) [brackets {:on-click toggle-collapse} nil]
                                    [brackets {:on-click toggle-collapse} [collapsable-box {:state state} (map-indexed (fn [idx e] [:div.box-element
                                                                                                                                    (render-node e)
                                                                                                                                    (if (not= idx tail-idx) (list [:div.toggle-layout.comma {:on-click toggle-layout} ","]
                                                                                                                                                                  (white-space-optional)))])
                                                                                                                       elements)]])]))))


;; Map node type to render function
(def type-render {"Program"                  program-render
                  "EmptyStatement"           empty-statement-render
                  "DebuggerStatement"        debugger-statement-render
                  "ReturnStatement"          return-statement-render
                  "BlockStatement"           block-statement-render
                  "BreakStatement"           break-statement-render
                  "ContinueStatement"        continue-statement-render
                  "LabeledStatement"         labeled-statement-render
                  "ExpressionStatement"      expression-statement-render
                  "ThrowStatement"           throw-statement-render
                  "WhileStatement"           while-statement-render
                  "DoWhileStatement"         do-while-statement-render
                  "IfStatement"              if-statement-render
                  "ImportDeclaration"        import-declaration-render
                  "ImportDefaultSpecifier"   import-default-specifier-render
                  "ImportNamespaceSpecifier" import-namespace-specifier-render
                  "ImportSpecifier"          import-specifier-render
                  "ExportDefaultDeclaration" export-default-declaration-render
                  "TryStatement"             try-statement-render
                  "WithStatement"            with-statement-render
                  "VariableDeclaration"      variable-declaration-render
                  "VariableDeclarator"       variable-declarator-render
                  "CatchClause"              catch-clause-render
                  "ForStatement"             for-statement-render
                  "ForOfStatement"           for-of-statement-render
                  "FunctionDeclaration"      function-declaration-render
                  "ClassDeclaration"         class-declaration-render
                  "ClassBody"                class-body-render
                  "MethodDefinition"         method-definition-render
                  "SwitchStatement"          switch-statement-render
                  "SwitchCase"               switch-case-render
                  "ArrayExpression"          array-expression-render
                  "Identifier"               identifier-render
                  "Literal"                  literal-render})

(defn ast-render [ast]
  (render-node ast))
