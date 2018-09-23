;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-import
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              asterisk
                                              comma
                                              semicolon
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ImportDeclaration
(defn import-declaration-render [node]
  (let [id (id-of node)
        specifiers (get node "specifiers")
        source (get node "source")]
    [:div {:class "import declaration"}
     (js-keyword "import")
     (if (> (count specifiers) 0) [:div
                                   (white-space)
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
                                         render-list (fn [import-specifier-list] (smart-box {:id            id
                                                                                             :pair          :brace
                                                                                             :seperator     :comma
                                                                                             :init-collapse false
                                                                                             :init-layout   "horizontal"} import-specifier-list))]
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
                                            (= second-sp-type "ImportNamespaceSpecifier")) [:div
                                                                                            (render-node first-sp)
                                                                                            (comma)
                                                                                            (white-space-optional)
                                                                                            (render-node second-sp)]
                                       ;; case 5
                                       (and (> (count specifiers) 1)
                                            (= first-sp-type "ImportDefaultSpecifier")
                                            (every? #(= (get %1 "type") "ImportSpecifier") rest-sps)) [:div
                                                                                                       (render-node first-sp)
                                                                                                       (comma)
                                                                                                       (white-space-optional)
                                                                                                       (render-list rest-sps)]
                                       ;; UNKNOWN
                                       true (str "Unhandled case: " specifiers)))
                                   (white-space)
                                   (js-keyword "from")])
     (white-space)
     (render-node source)
     (white-space-optional)
     (semicolon)]))

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
                            [:div
                             (render-node imported)
                             (white-space)
                             (js-keyword "as")
                             (white-space)
                             (render-node local)]))])

;; ImportNamespaceSpecifier
(defn import-namespace-specifier-render [node]
  [:div {:class "import-namespace-specifier"}
   "*" (white-space) (js-keyword "as") (white-space) (render-node (get node "local"))])


(def demo ["import a1 from 'm'"
           "import * as m1 from 'm'"
           "import {x1, y1} from 'm'"
           "import a2, {x2, y2} from 'm'"
           "import a3, * as m2 from 'm'"])
