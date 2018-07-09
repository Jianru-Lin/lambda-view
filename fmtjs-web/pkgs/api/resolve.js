var Module = require('module')
var path = require('path')
var native_module_source = process.binding ("natives")

module.exports = function(base, target) {
	try {
		if (!target) {
			return native_path(base) || require.resolve(base)
		}
		else if (target[0] === '.' || target[0] === '/') {
			var base_filename = Module._resolveFilename(base, module, false)
			var base_dirname = path.dirname(base_filename)
			var target_filename = path.resolve(base_dirname, target)
			target_filename = Module._resolveFilename(target_filename, module, false)
			return target_filename
		}			
		else {
			return native_path(target) || require.resolve(target)
		}
	}
	catch (err) {
		console.error(err)
		return undefined
	}

	function native_path(name) {
		if (typeof native_module_source[name] === 'string') {
			return name
		}
		else {
			return false
		}
	}
}
