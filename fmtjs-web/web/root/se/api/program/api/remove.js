export default function remove(state) {
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