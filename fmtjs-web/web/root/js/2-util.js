function log(type, text) {
	if (type === 'info') return
	var div = document.createElement('div')
	div.setAttribute('class', type)
	div.textContent = '[?1] ?2'.replace('?1', type).replace('?2', text)
	$('#log').append(div)
}