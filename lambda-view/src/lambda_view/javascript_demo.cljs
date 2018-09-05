(ns lambda-view.javascript-demo)

(defn defdemo [& demos]
  (clojure.string/join "\n" demos))

(def literal (defdemo "undefined;"
                      "null;"
                      "true;"
                      "1.23e4;"
                      "\"Hello World!\";"
                      "[0,1,2];"
                      "({key: value});"))

(def array-expression (defdemo "[];"
                               "[1];"
                               "[1, 2];"
                               "[1, 2, 3];"
                               "[1, 2, 3, 4];"
                               "[1, 2, 3, 4, 5];"
                               "[1, 2, 3, 4, 5, 6];"))

(def block-statement (defdemo "{}"
                              "{label: statement}"))

(def import-declaration (clojure.string/join "\n" ["import a1 from 'm'"
                                                   "import * as m1 from 'm'"
                                                   "import {x1, y1} from 'm'"
                                                   "import a2, {x2, y2} from 'm'"
                                                   "import a3, * as m2 from 'm'"]))

;; (def import-declaration2 (clojure.string/join "\n" ["import {x, y} from 'm'"]))

(def export-declaration (clojure.string/join "\n" ["export default a"]))

(def try-statement (clojure.string/join "\n" ["try {a} catch {b}"
                                              "try {a} finally {b}"
                                              "try {a} catch {b} finally {c}"
                                              "try {a} catch(e) {b} finally {c}"]))

(def with-statement (clojure.string/join "\n" ["with (x) {y}"]))

(def variable-declaration (defdemo "var a1"
                                   "var a2 = 1"
                                   "var a3 = 1, a4 = 2"
                                   "let b1"
                                   "let b2 = 1"
                                   "let b3 = 1, b4 = 2"
                                   ;; "const x" is invalid
                                   "const c1 = 1"
                                   "const c2 = 1, c3 = 2"))

(def for-statement
  (defdemo "for(;;) 1"
           "for(u;;) 1"
           "for(;v;) 1"
           "for(;;w) 1"
           "for(a;b;c) 1"
           "for (a;b;c) {1}"))

(def for-of-statement
  (defdemo "for(a of b) 1"
           "for(a of b) {1}"
           ;; TODO "for await (const x of xs) {}"
           ))

(def function-declration
  (defdemo "function a1() {}"
           "function a2(p2) {}"
           "function a3(u3,v4) {}"
           "async function a4() {}"
           "function *g1() {}"
           "async function *g2() {}"))

(def class-declaration
  (defdemo
    ;"class a1 {}"
    ;"class a2 extends b2 {}"
    ;"class a3 { constructor(a, b) {1} }"
    ;"class a4 { m1(a, b) {1} async m2(a, b) {2} * m3 (a, b) {3} async * m4(a, b) {4} }"
    "class a5 { get m() {1} }"
    "class a6 { static get m() {1} }"
    "class a7 { set m(v) {1} }"
    "class a8 { static set m(v) {1} }"
    ;"class a9 { static m(a, b) {1} }"
    ))

(def switch-statement
  (defdemo "switch(a) {}"
           "switch(a) {default: 1}"
           "switch(a) {case \"a\": case \"b\": 1}"
           "switch(a) {case \"a\": 1; case \"b\": 2}"))

(def while-statement
  (defdemo "while(1) {2}"))

(def do-while-statement
  (defdemo "do {1} while(2)"))

(def if-statement
  (defdemo "if (1) 2"
           "if (1) {2}"
           ;; "if (1) 2 else 3" NOT WORKING, Invalid Grammer
           "if (1) {2} else 3"
           "if (1) {2} else {3}"
           "if (1) {2} else if (3) {4}"
           "if (1) {2} else if (3) {4} else {5}"))

(def assignment-expression
  (defdemo "a = 1"
           "c = d = 2"))

(def binary-expression
  (defdemo "a + b;"
           "a - b;"
           "a + b * c;"
           "(a + b) * c;"))

(def this-expression
  (defdemo "this;"))

(def sequence-expression
  (defdemo "a, b;"
           ;"a, b, c;"
           ;"a, (b, c);"
           ))

(defonce state (atom {:current nil}))

(defn current [] (:current @state))

;; auto refresh
(swap! state assoc :current sequence-expression)
