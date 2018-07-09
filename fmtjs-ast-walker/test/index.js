var assert = require('chai').assert
var demo = require('../fmtjs-demo')
var walk = require('../index').walk

describe('walk', function() {
	describe('empty program', function() {
		it('without any error', function() {
			var ast = demo.load_ast('empty')
			var callout = {
				enter: function() {},
				leave: function() {}
			}
			walk(ast, callout)
		})

		it('accept null callout', function() {
			var ast = demo.load_ast('empty')
			var callout = null
			walk(ast, callout)
		})

		it('fire enter', function() {
			var fired = false
			var ast = demo.load_ast('empty')
			var callout = {
				enter: function() {
					fired = true
				},
				leave: function() {}
			}
			walk(ast, callout)
			assert.isOk(fired, 'enter is not fired')
		})

		it('fire leave', function() {
			var fired = false
			var ast = demo.load_ast('empty')
			var callout = {
				enter: function() {},
				leave: function() {
					fired = true
				}
			}
			walk(ast, callout)
			assert.isOk(fired, 'leave is not fired')
		})
	});

	demo.list().forEach(function(target) {

		describe(target, function() {
			it('without any error', function() {
				var ast = demo.load_ast(target)
				var callout = {
					enter: function() {},
					leave: function() {}
				}
				walk(ast, callout)
			})

			it('fire enter', function() {
				var fired = false
				var ast = demo.load_ast(target)
				var callout = {
					enter: function() {
						fired = true
					},
					leave: function() {}
				}
				walk(ast, callout)
				assert.isOk(fired, 'enter is not fired')
			})

			it('fire leave', function() {
				var fired = false
				var ast = demo.load_ast(target)
				var callout = {
					enter: function() {},
					leave: function() {
						fired = true
					}
				}
				walk(ast, callout)
				assert.isOk(fired, 'leave is not fired')
			})
		})
		
	})
})