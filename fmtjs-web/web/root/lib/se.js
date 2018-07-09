/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.l = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };

/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};

/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};

/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 6);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__state__ = __webpack_require__(5);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1__api_remove_js__ = __webpack_require__(2);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_2__api_declaration_js__ = __webpack_require__(1);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_3__api_statement_js__ = __webpack_require__(4);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_4__api_render_js__ = __webpack_require__(3);
/* harmony export (immutable) */ exports["a"] = program;






function program(ctx) {
	return function(cb) {
		var self = {}
		var s = __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_0__state__["a" /* default */])()

		self.remove = __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_1__api_remove_js__["a" /* default */])(s)
		self.declaration = __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_2__api_declaration_js__["a" /* default */])(s)
		self.statement = __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_3__api_statement_js__["a" /* default */])(s)
		self.render = __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_4__api_render_js__["a" /* default */])(s)

		// auto invoke render
		self.render(ctx.container)

		if (cb) cb(self)

		return self
	}
}

/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ exports["a"] = declaration;
function declaration(state) {
	return function() {
		// todo
	}
}

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ exports["a"] = remove;
function remove(state) {
	return function() {
		if (arguments.length === 0) {
			// remove self from parent
			if (state.parent) {
				state.parent.remove(this)
			}
		}
		else {
			// remove child
			var child = arguments[0]
			// TODO
		}		
	}
}

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ exports["a"] = render;
function render(state) {
	return function(container) {
		// if rendered, skip
		// TODO: what if container changed?
		if (state.dom) return

		// create dom structure
		var dom = state.dom = document.createElement('div')
		dom.classList.add('program')
		
		// render children
		if (state.children) {
			state.children.forEach(function(child) {
				child.render(state.dom)
			})
		}
		// append to container
		if (container) {
			container.appendChild(dom)
		}

		return state.dom
	}
}

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ exports["a"] = statement;
function statement(state) {
	return function() {
		
	}
}

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ exports["a"] = state;
function state() {
	return {
		dom: null,
		container: null,
		parent: null,
		children: [],
	}
}

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__api_program_index_js__ = __webpack_require__(0);


window.se = window.se || function(container) {
	var ctx = {
		container: container ? document.querySelector(container) : null
	}

	return {
		program: __webpack_require__.i(__WEBPACK_IMPORTED_MODULE_0__api_program_index_js__["a" /* default */])(ctx)
	}
}

/***/ })
/******/ ]);