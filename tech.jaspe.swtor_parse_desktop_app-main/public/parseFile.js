const fs = require('fs');
const readline = require('readline');
const path = require('path');
const { fork } = require('child_process');
const eol = require('eol')
const parseFilePath = path.join(__dirname, 'parse.js');
const parse = fork(parseFilePath);
const stream = require('stream');

let readBytes = 0;
let waitToReadFile = 500;
let fileDescriptor = null;

function parseLogs(path, files, lines = []) {
	const fileName = files.shift();

	const fileStream = fs.createReadStream((path + '/' + fileName).replace(/\\/g, '/'), {
		encoding: 'latin1'
	});

	const rl = readline.createInterface({
		input: fileStream,
		crlfDelay: Infinity
	});

	rl.on('line', function (line) {
		lines = [...lines, line];
	});

	rl.on('close', function () {
		if (files.length > 0) {
			parseLogs(path, files, lines);
		} else {
			parse.send(JSON.stringify({
				type: 'parse_logs',
				data: {
					lines: lines,
					fileName: fileName
				}
			}));
		}
	});
}

////
// Continuously read for file changes in the combat file.
// If there's not stats changes in the file, it will wait for 100ms
// to read again. 
////
function realTimeParse(fullFilePath, fileName) {
	//---- Code to read file in realtime line by line
	const bufferStream = new stream.PassThrough();

	const rl = readline.createInterface({
		input: bufferStream,
		crlfDelay: Infinity
	});

	rl.on('line', function (line) {
		console.log('READLINE');

		parse.send(JSON.stringify({
			type: 'parse_line',
			data: {
				lines: [line],
				fileName: fileName
			}
		}));
	});

	rl.on('close', function () {
		//TODO: Send message that realtime is off.
		console.log('BUFFER STREAM CLOSE');
		close();
	});

	const read = (err, fd) => {
		if (fd === null) {
			bufferStream.end();
			return;
		}

		fileDescriptor = fd;

		try {
			if (err) {
				console.error(err);
				bufferStream.end();
				return;
			}

			const stats = fs.fstatSync(fileDescriptor);

			if (stats.size < readBytes + 1) {
				setTimeout(() => read(err, fileDescriptor), waitToReadFile);
				return;
			}

			fs.read(fileDescriptor, readBytes, (err, byteCount, buff) => {
				try {
					if (err) {
						console.error(err);
						bufferStream.end();
						return;
					}

					bufferStream.write(buff.toString('latin1', 0, byteCount))
					readBytes += byteCount;
					read(err, fileDescriptor);
				} catch (err) {
					console.error(err);
					bufferStream.end();
				}
			});
		} catch (err) {
			console.error(err);
			bufferStream.end();
		}
	};

	//---- Read the entire file before starting to read file in realtime
	let lines = [];

	const rl2 = readline.createInterface({
		input: fs.createReadStream(fullFilePath.replace(/\\/g, '/'), {
			encoding: 'latin1'
		}),
		crlfDelay: Infinity
	});

	rl2.on('line', function (line) {
		readBytes += Buffer.byteLength(line)
		lines = [...lines, line];
	});

	rl2.on('close', function () {
		parse.send(JSON.stringify({
			type: 'parse_full_file',
			data: {
				lines: lines,
				fileName: fileName
			}
		}));
		fs.open(fullFilePath.replace(/\\/g, '/'), 'r', read);
	});
}

////
// Close the file descriptor obtained from opening the combat log file
////
function close() {
	if (fileDescriptor === null) return;

	fs.close(fileDescriptor, (err) => {
		if (err) {
			console.error('Failed to close file', err);
			return;
		}
	});

	readBytes = 0;
	fileDescriptor = null;
}

parse.on("message", msg => {
	const message = JSON.parse(msg);

	switch (message.type) {
		case 'parse_logs': {
			postMessage(JSON.stringify({
				type: 'parse_logs',
				data: {
					file: message.data.file,
					stats: message.data.stats
				}
			}));
		} break;

		case 'parse_log_percentage': {
			postMessage(JSON.stringify({
				type: 'parse_log_percentage',
				data: message.data
			}));
		} break;

		case 'current_combat': {
			postMessage(JSON.stringify({
				type: 'current_combat',
				data: message.data
			}));
		} break;

		case 'ready_current_combat': {
			postMessage(JSON.stringify({
				type: 'ready_current_combat',
			}));
		} break;

		case 'info': {
			console.info(message.data);
		} break;

		case 'error': {
			console.error(message.data);
		} break;
	}
});

parse.on('error', (err) => {
	console.error(err);
});

onmessage = function (e) {
	const message = JSON.parse(e.data);

	switch (message.type) {
		case 'parse_logs': {
			parseLogs(message.path, message.files)
		} break;

		case 'real_time': {
			close();
			realTimeParse(message.path + '/' + message.selectedFile, message.selectedFile)
		} break;


		case 'stop_real_time': {
			close();
		} break;

		case 'current_combat': {
			parse.send(JSON.stringify({ type: 'current_combat' }));
		} break;
	}
};
