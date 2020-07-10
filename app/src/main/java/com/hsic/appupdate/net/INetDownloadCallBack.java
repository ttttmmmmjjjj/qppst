package com.hsic.appupdate.net;

import java.io.File;

/**
 * 下载文件接口
 */
public interface INetDownloadCallBack {

    void success(File apkFile);
    void failed(String throwable);
    void progress(String progress);
}
