;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.core
  (:require [lambda-view.javascript.bridge :as bridge]
            [lambda-view.javascript.basic.t-literal :as t-literal]
            [lambda-view.javascript.basic.t-identifier :as t-identifier]
            [lambda-view.javascript.basic.t-program :as t-program]
            [lambda-view.javascript.basic.t-template-literal :as t-template-literal]
            [lambda-view.javascript.declaration.t-class :as t-class]
            [lambda-view.javascript.declaration.t-export-all :as t-export-all]
            [lambda-view.javascript.declaration.t-export-default :as t-export-default]
            [lambda-view.javascript.declaration.t-export-named :as t-export-named]
            [lambda-view.javascript.declaration.t-function :as t-function]
            [lambda-view.javascript.declaration.t-import :as t-import]
            [lambda-view.javascript.declaration.t-variable :as t-variable]
            [lambda-view.javascript.statement.t-block :as t-block]
            [lambda-view.javascript.statement.t-break :as t-break]
            [lambda-view.javascript.statement.t-continue :as t-continue]
            [lambda-view.javascript.statement.t-debugger :as t-debugger]
            [lambda-view.javascript.statement.t-do-while :as t-do-while]
            [lambda-view.javascript.statement.t-empty :as t-empty]
            [lambda-view.javascript.statement.t-expression :as t-expression]
            [lambda-view.javascript.statement.t-for :as t-for]
            [lambda-view.javascript.statement.t-for-in :as t-for-in]
            [lambda-view.javascript.statement.t-for-of :as t-for-of]
            [lambda-view.javascript.statement.t-if :as t-if]
            [lambda-view.javascript.statement.t-labeled :as t-labeled]
            [lambda-view.javascript.statement.t-return :as t-return]
            [lambda-view.javascript.statement.t-switch :as t-switch]
            [lambda-view.javascript.statement.t-throw :as t-throw]
            [lambda-view.javascript.statement.t-try :as t-try]
            [lambda-view.javascript.statement.t-while :as t-while]
            [lambda-view.javascript.statement.t-with :as t-with]
            [lambda-view.javascript.expression.t-array :as t-array]
            [lambda-view.javascript.expression.t-arrow-function :as t-arrow-function]
            [lambda-view.javascript.expression.t-await :as t-await]
            [lambda-view.javascript.expression.t-assignment :as t-assignment]
            [lambda-view.javascript.expression.t-binary :as t-binary]
            [lambda-view.javascript.expression.t-call :as t-call]
            [lambda-view.javascript.expression.t-conditional :as t-conditional]
            [lambda-view.javascript.expression.t-function :as t-function-exp]
            [lambda-view.javascript.expression.t-logical :as t-logical]
            [lambda-view.javascript.expression.t-member :as t-member]
            [lambda-view.javascript.expression.t-new :as t-new]
            [lambda-view.javascript.expression.t-object :as t-object]
            [lambda-view.javascript.expression.t-sequence :as t-sequence]
            [lambda-view.javascript.expression.t-tagged-template :as t-tagged-template]
            [lambda-view.javascript.expression.t-this :as t-this]
            [lambda-view.javascript.expression.t-unary :as t-unary]
            [lambda-view.javascript.expression.t-update :as t-update]
            [lambda-view.javascript.expression.t-yield :as t-yield]))

;; Map node type to render function
(def type-render {"Identifier"               t-identifier/render
                  "Literal"                  t-literal/render
                  "Program"                  t-program/render
                  "TemplateLiteral"          t-template-literal/template-literal-render
                  "TemplateElement"          t-template-literal/template-element-render
                  ; Declaration
                  "ClassDeclaration"         t-class/class-declaration-render
                  "ClassBody"                t-class/class-body-render
                  "MethodDefinition"         t-class/method-definition-render
                  "ExportAllDeclaration"     t-export-all/render
                  "ExportDefaultDeclaration" t-export-default/render
                  "ExportNamedDeclaration"   t-export-named/export-named-render
                  "ExportSpecifier"          t-export-named/export-specifier-render
                  "FunctionDeclaration"      t-function/render
                  "ImportDeclaration"        t-import/import-declaration-render
                  "ImportDefaultSpecifier"   t-import/import-default-specifier-render
                  "ImportNamespaceSpecifier" t-import/import-namespace-specifier-render
                  "ImportSpecifier"          t-import/import-specifier-render
                  "VariableDeclaration"      t-variable/variable-declaration-render
                  "VariableDeclarator"       t-variable/variable-declarator-render
                  ; Statement
                  "BlockStatement"           t-block/render
                  "BreakStatement"           t-break/render
                  "ContinueStatement"        t-continue/render
                  "DebuggerStatement"        t-debugger/render
                  "DoWhileStatement"         t-do-while/render
                  "EmptyStatement"           t-empty/render
                  "ExpressionStatement"      t-expression/render
                  "ForStatement"             t-for/render
                  "ForInStatement"           t-for-in/render
                  "ForOfStatement"           t-for-of/render
                  "IfStatement"              t-if/render
                  "LabeledStatement"         t-labeled/render
                  "ReturnStatement"          t-return/render
                  "SwitchStatement"          t-switch/switch-statement-render
                  "SwitchCase"               t-switch/switch-case-render
                  "ThrowStatement"           t-throw/render
                  "TryStatement"             t-try/try-statement-render
                  "CatchClause"              t-try/catch-clause-render
                  "WhileStatement"           t-while/render
                  "WithStatement"            t-with/render
                  ;; Expression
                  "ArrayExpression"          t-array/render
                  "ArrowFunctionExpression"  t-arrow-function/render
                  "AwaitExpression"          t-await/render
                  "AssignmentExpression"     t-assignment/render
                  "BinaryExpression"         t-binary/render
                  "CallExpression"           t-call/render
                  "ConditionalExpression"    t-conditional/render
                  "FunctionExpression"       t-function-exp/render
                  "LogicalExpression"        t-logical/render
                  "MemberExpression"         t-member/render
                  "NewExpression"            t-new/render
                  "ObjectExpression"         t-object/object-expression-render
                  "Property"                 t-object/property-render
                  "SpreadElement"            t-object/spread-element
                  "SequenceExpression"       t-sequence/render
                  "TaggedTemplateExpression" t-tagged-template/render
                  "ThisExpression"           t-this/render
                  "UnaryExpression"          t-unary/render
                  "UpdateExpression"         t-update/render
                  "YieldExpression"          t-yield/render})


;; Map node type to demo
(def type-demo {"Identifier"               t-identifier/demo
                "Literal"                  t-literal/demo
                "Program"                  t-program/demo
                "TemplateLiteral"          t-template-literal/demo
                ; Declaration
                "ClassDeclaration"         t-class/demo
                "ExportAllDeclaration"     t-export-all/demo
                "ExportDefaultDeclaration" t-export-default/demo
                "ExportNamedDeclaration"   t-export-named/demo
                "FunctionDeclaration"      t-function/demo
                "ImportDeclaration"        t-import/demo
                "VariableDeclaration"      t-variable/demo
                ; Statement
                "BlockStatement"           t-block/demo
                "BreakStatement"           t-break/demo
                "ContinueStatement"        t-continue/demo
                "DebuggerStatement"        t-debugger/demo
                "DoWhileStatement"         t-do-while/demo
                "EmptyStatement"           t-empty/demo
                "ExpressionStatement"      t-expression/demo
                "ForStatement"             t-for/demo
                "ForInStatement"           t-for-in/demo
                "ForOfStatement"           t-for-of/demo
                "IfStatement"              t-if/demo
                "LabeledStatement"         t-labeled/demo
                "ReturnStatement"          t-return/demo
                "SwitchStatement"          t-switch/demo
                "ThrowStatement"           t-throw/demo
                "TryStatement"             t-try/demo
                "WhileStatement"           t-while/demo
                "WithStatement"            t-with/demo
                ;; Expression
                "ArrayExpression"          t-array/demo
                "ArrowFunctionExpression"  t-arrow-function/demo
                "AwaitExpression"          t-await/demo
                "AssignmentExpression"     t-assignment/demo
                "BinaryExpression"         t-binary/demo
                "CallExpression"           t-call/demo
                "ConditionalExpression"    t-conditional/demo
                "FunctionExpression"       t-function-exp/demo
                "LogicalExpression"        t-logical/demo
                "MemberExpression"         t-member/demo
                "NewExpression"            t-new/demo
                "ObjectExpression"         t-object/demo
                "SequenceExpression"       t-sequence/demo
                "TaggedTemplateExpression" t-tagged-template/demo
                "ThisExpression"           t-this/demo
                "UnaryExpression"          t-unary/demo
                "UpdateExpression"         t-update/demo
                "YieldExpression"          t-yield/demo})

(bridge/setup-render-imp-of-type (fn [type] (get type-render type)))

(defn ast-render [ast]
  (bridge/call-render-node ast))

;; Demo

(defonce state (atom {:current nil}))

(defn current [] (:current @state))

;; auto refresh
(swap! state assoc :current (clojure.string/join "\n" t-array/demo))

