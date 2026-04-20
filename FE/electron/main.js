"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
const child_process_1 = require("child_process");
const fs_1 = require("fs");
const path = __importStar(require("path"));
const fs = __importStar(require("fs"));
const os = __importStar(require("os"));
const electron_updater_1 = require("electron-updater");
// ─── Globals ─────────────────────────────────────────────────────────────────
let mainWin = null;
let loadingWin = null;
let springProcess = null;
let logStream;
// ─── Loading Messages (UX CLEAN) ─────────────────────────────────────────────
const LoadingMessages = {
    CHECK_UPDATE: 'Controllo aggiornamenti...',
    NO_UPDATE: 'Nessun aggiornamento disponibile',
    UPDATE_FOUND: (v) => `Aggiornamento ${v} trovato`,
    DOWNLOADING: (pct) => `Download aggiornamento... ${pct}%`,
    INSTALLING: 'Installazione aggiornamento...',
    PREPARING: 'Preparazione servizi...',
    STARTING: 'Avvio in corso...',
    FINALIZING: 'Quasi pronto...',
    ERROR: 'Errore imprevisto',
};
// ─── Logger ─────────────────────────────────────────────────────────────────
function initLogger() {
    const logDir = path.join(os.homedir(), 'AppData', 'Local', 'AtelierPerDigital');
    if (!fs.existsSync(logDir))
        fs.mkdirSync(logDir, { recursive: true });
    const logPath = path.join(logDir, 'app.log');
    logStream = (0, fs_1.createWriteStream)(logPath, { flags: 'a' });
    const write = (level, args) => {
        const line = `[${new Date().toISOString()}] [${level}] ${args.map(String).join(' ')}\n`;
        logStream.write(line);
    };
    console.log = (...args) => write('INFO', args);
    console.warn = (...args) => write('WARN', args);
    console.error = (...args) => write('ERROR', args);
    process.on('unhandledRejection', (reason) => {
        var _a;
        console.error((_a = reason === null || reason === void 0 ? void 0 : reason.stack) !== null && _a !== void 0 ? _a : reason);
        electron_1.dialog.showErrorBox('Errore critico', String(reason));
        setTimeout(() => electron_1.app.exit(1), 1000);
    });
    process.on('uncaughtException', (err) => {
        var _a;
        console.error((_a = err.stack) !== null && _a !== void 0 ? _a : err);
        electron_1.dialog.showErrorBox('Errore critico', String(err));
        setTimeout(() => electron_1.app.exit(1), 1000);
    });
}
// ─── Utils ───────────────────────────────────────────────────────────────────
function getResourcesPath() {
    return electron_1.app.isPackaged
        ? path.join(process.resourcesPath, 'resources')
        : path.join(__dirname, '../resources');
}
function ensureDataDirectory() {
    const dataDir = path.join(os.homedir(), 'AppData', 'Local', 'AtelierPerDigital');
    if (!fs.existsSync(dataDir))
        fs.mkdirSync(dataDir, { recursive: true });
}
// ─── Loading Window ──────────────────────────────────────────────────────────
function createLoadingWindow() {
    return new Promise((resolve) => {
        loadingWin = new electron_1.BrowserWindow({
            width: 420,
            height: 240,
            frame: false,
            resizable: false,
            center: true,
            alwaysOnTop: true,
            show: false,
            backgroundColor: '#1a1a2e',
            webPreferences: {
                preload: path.join(__dirname, 'preload.js'),
                contextIsolation: true,
            },
        });
        loadingWin.loadFile(path.join(__dirname, 'loading.html'));
        loadingWin.once('ready-to-show', () => {
            loadingWin === null || loadingWin === void 0 ? void 0 : loadingWin.show();
            resolve();
        });
    });
}
function setLoadingMessage(message, percent, state = 'checking') {
    if (!loadingWin || loadingWin.isDestroyed())
        return;
    console.log(`[LOADING] ${message} (${percent !== null && percent !== void 0 ? percent : 0}%)`);
    loadingWin.webContents.send('update-loading', {
        message,
        percent,
        state,
    });
}
function closeLoadingWindow() {
    if (loadingWin && !loadingWin.isDestroyed()) {
        loadingWin.close();
        loadingWin = null;
    }
}
// ─── Auto Updater ────────────────────────────────────────────────────────────
function checkForUpdate() {
    return new Promise((resolve) => {
        let updateFound = false;
        let resolved = false;
        const done = (value) => {
            if (resolved)
                return;
            resolved = true;
            electron_updater_1.autoUpdater.removeAllListeners();
            resolve(value);
        };
        electron_updater_1.autoUpdater.on('checking-for-update', () => {
            setLoadingMessage(LoadingMessages.CHECK_UPDATE, 0, 'checking');
        });
        electron_updater_1.autoUpdater.on('update-not-available', () => {
            setLoadingMessage(LoadingMessages.NO_UPDATE, 100, 'done');
            setTimeout(() => done(false), 800);
        });
        electron_updater_1.autoUpdater.on('update-available', (info) => {
            updateFound = true;
            setLoadingMessage(LoadingMessages.UPDATE_FOUND(info.version), 0, 'downloading');
        });
        electron_updater_1.autoUpdater.on('download-progress', (progress) => {
            const pct = Math.round(progress.percent);
            setLoadingMessage(LoadingMessages.DOWNLOADING(pct), pct, 'downloading');
        });
        electron_updater_1.autoUpdater.on('update-downloaded', () => {
            setLoadingMessage(LoadingMessages.INSTALLING, 100, 'installing');
            done(true);
            setTimeout(() => {
                electron_updater_1.autoUpdater.quitAndInstall(false, true);
            }, 2500);
        });
        electron_updater_1.autoUpdater.on('error', (err) => {
            console.error(err);
            setLoadingMessage(LoadingMessages.ERROR, 0, 'error');
            done(false);
        });
        setTimeout(() => {
            if (!updateFound)
                done(false);
        }, 15000);
        electron_updater_1.autoUpdater.checkForUpdates().catch(() => done(false));
    });
}
// ─── Spring Boot ─────────────────────────────────────────────────────────────
function startSpringBoot() {
    return new Promise((resolve) => {
        var _a, _b;
        const resourcesPath = getResourcesPath();
        const javaPath = path.join(resourcesPath, 'jre', 'bin', process.platform === 'win32' ? 'java.exe' : 'java');
        const jarPath = path.join(resourcesPath, 'app.jar');
        setLoadingMessage(LoadingMessages.PREPARING, 20, 'starting');
        springProcess = (0, child_process_1.spawn)(javaPath, ['-jar', jarPath]);
        const onData = (data) => {
            const output = data.toString();
            console.log(output);
            if (output.includes('Started') || output.includes('Tomcat started')) {
                setLoadingMessage(LoadingMessages.STARTING, 70, 'starting');
                resolve();
            }
        };
        (_a = springProcess.stdout) === null || _a === void 0 ? void 0 : _a.on('data', onData);
        (_b = springProcess.stderr) === null || _b === void 0 ? void 0 : _b.on('data', onData);
        springProcess.on('error', (err) => {
            console.error(err);
            setLoadingMessage(LoadingMessages.ERROR, 0, 'error');
            electron_1.dialog.showErrorBox('Errore backend', err.message);
            resolve();
        });
        setTimeout(resolve, 30000);
    });
}
// ─── Main Window ─────────────────────────────────────────────────────────────
function createMainWindow() {
    mainWin = new electron_1.BrowserWindow({
        width: 1280,
        height: 800,
        show: false,
        webPreferences: {
            preload: path.join(__dirname, 'preload.js'),
            contextIsolation: true,
        },
    });
    if (electron_1.app.isPackaged) {
        mainWin.loadFile(path.join(process.resourcesPath, 'app.asar.unpacked', 'dist', 'browser', 'index.html'));
    }
    else {
        mainWin.loadURL('http://localhost:4200');
    }
    mainWin.once('ready-to-show', () => {
        setLoadingMessage(LoadingMessages.FINALIZING, 100, 'done');
        setTimeout(() => {
            closeLoadingWindow();
            mainWin === null || mainWin === void 0 ? void 0 : mainWin.show();
        }, 500);
    });
}
// ─── App Lifecycle ───────────────────────────────────────────────────────────
electron_1.app.whenReady().then(() => __awaiter(void 0, void 0, void 0, function* () {
    initLogger();
    try {
        yield createLoadingWindow();
        if (electron_1.app.isPackaged) {
            ensureDataDirectory();
            const updating = yield checkForUpdate();
            if (!updating) {
                yield startSpringBoot();
                createMainWindow();
            }
        }
        else {
            closeLoadingWindow();
            createMainWindow();
        }
    }
    catch (err) {
        setLoadingMessage(LoadingMessages.ERROR, 0, 'error');
        electron_1.dialog.showErrorBox('Errore avvio', String(err));
        electron_1.app.quit();
    }
}));
// ─── Quit ────────────────────────────────────────────────────────────────────
electron_1.app.on('before-quit', () => {
    if (springProcess) {
        springProcess.kill();
        springProcess = null;
    }
    logStream === null || logStream === void 0 ? void 0 : logStream.end();
});
electron_1.app.on('window-all-closed', () => {
    if (process.platform !== 'darwin')
        electron_1.app.quit();
});
