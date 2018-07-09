import program from "./api/program/index.js";

window.se = window.se || function(container) {
	var ctx = {
		container: container ? document.querySelector(container) : null
	}

	return {
		program: program(ctx)
	}
}