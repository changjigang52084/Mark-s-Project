package com.hch.filedownloader.i;

import com.hch.filedownloader.model.FileDownloadTransferModel;

interface IFileDownloadIPCCallback {
    oneway void callback(in FileDownloadTransferModel transfer);
}
