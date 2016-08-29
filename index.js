#!/usr/bin/env node
var helper = require('./helper')
var path = require('path')
var fmtjs = require('fmtjs')
var args = require('minimist')(process.argv.slice(2))
var log = helper.log

run()

function run() {
	if (args.help || args.h) {
		print_usage()
	}
	else if (args.version || args.v) {
		print_version()
	}
	else {
		compile(args._[0])
	}
}

function compile(in_filename) {
	try {
		in_filename = path.resolve(in_filename)
		var in_filename_base = path.basename(in_filename)
		log.info('read file from ' + JSON.stringify(in_filename) + "...")
		var in_content = helper.load_utf8_file(in_filename)
		log.info('parsing javascript source code and generating html content...')
		var out_content = fmtjs(in_content, {mode: 'html', filename: in_filename_base})
		// log.info('calculating hash...')
		var out_filename = 'lambda-view-' + helper.hash(out_content) + '-' + in_filename_base + '.html'
		out_filename = path.resolve(helper.tmp_dir(), out_filename)
		log.info('write file to ' + JSON.stringify(out_filename) + "...")
		helper.save_utf8_file(out_filename, out_content)
		log.info('try opening it using your default web browser...')
		var ok = helper.open_html_file(out_filename)
		log.info(ok ? 'done :)' : 'failed, try opening it manually please :(')
	}
	catch (err) {
		log.error(err.toString())
	}
}

function print_version() {
	var p = require('./package.json')
	log.info(`${p.name} v${p.version} by ${p.author} (${p.email})`)
}

function print_usage() {
	log.info('Usage: lv <file.js>')
}