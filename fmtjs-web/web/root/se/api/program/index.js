import state from './state'
import remove from './api/remove.js'
import declaration from './api/declaration.js'
import statement from './api/statement.js'
import render from './api/render.js'

export default function program(ctx) {
	return function(cb) {
		var self = {}
		var s = state()

		self.remove = remove(s)
		self.declaration = declaration(s)
		self.statement = statement(s)
		self.render = render(s)

		// auto invoke render
		self.render(ctx.container)

		if (cb) cb(self)

		return self
	}
}