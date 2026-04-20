"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const electron_1 = require("electron");
electron_1.contextBridge.exposeInMainWorld('api', {
    onUpdateLoading: (callback) => {
        electron_1.ipcRenderer.on('update-loading', (_, data) => callback(data));
    },
});
