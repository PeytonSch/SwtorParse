const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');

// Live Reload
require('electron-reload')(__dirname, {
	electron: path.join(__dirname, '../node_modules', '.bin', 'electron'),
	awaitWriteFinish: true,
});

// Handle creating/removing shortcuts on Windows when installing/uninstalling.
if (require('electron-squirrel-startup')) {
	// eslint-disable-line global-require
	app.quit();
}

const createWindow = () => {
	// Create the browser window.
	const mainWindow = new BrowserWindow({
		width: 960,
		height: 600,
		minHeight: 600,
		minWidth: 960,
		webPreferences: {
			contextIsolation: false,
			nodeIntegration: true,
			nodeIntegrationInWorker: true,
		},
	});

	// and load the index.html of the app.
	mainWindow.loadFile(path.join(__dirname, '../public/index.html'));

	// Open the DevTools.
	mainWindow.webContents.openDevTools();
};

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow);

// Quit when all windows are closed.
app.on('window-all-closed', () => {
	// On OS X it is common for applications and their menu bar
	// to stay active until the user quits explicitly with Cmd + Q
	if (process.platform !== 'darwin') {
		app.quit();
	}
});

app.on('activate', () => {
	// On OS X it's common to re-create a window in the app when the
	// dock icon is clicked and there are no other windows open.
	if (BrowserWindow.getAllWindows().length === 0) {
		createWindow();
	}
});

// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and import them here.
let personalOverlayWindow;

ipcMain.handle('start-parse', (event, args) => {
	if (typeof personalOverlayWindow !== 'undefined') {
		return;
	}

	personalOverlayWindow = new BrowserWindow({
		width: 150,
		height: 250,
		minWidth: 150,
		minHeight: 250,
		maxWidth: 210,
		maxHeight: 250,
		frame: false,
		transparent: true,
		backgroundColor: '#70151515',
		alwaysOnTop: true,
		skipTaskbar: true,
		webPreferences: {
			contextIsolation: false,
			nodeIntegration: true,
			nodeIntegrationInWorker: true,
		},
	});
	personalOverlayWindow.setAlwaysOnTop(true, 'normal');
	personalOverlayWindow.loadFile(path.join(__dirname, '../public/overlay.html'));
	personalOverlayWindow.show();
});

ipcMain.handle('close_personal_overlay', (event, args) => {
	personalOverlayWindow.close();
	personalOverlayWindow = undefined;
});

ipcMain.on('update_personal_stats', (_, args) => {
	if (typeof personalOverlayWindow === 'undefined') {
		return;
	}

	personalOverlayWindow.webContents.send('update_personal_stats', args);
});
