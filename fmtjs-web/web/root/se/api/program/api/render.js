export default function render(state) {
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