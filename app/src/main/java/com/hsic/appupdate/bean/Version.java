package com.hsic.appupdate.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/9/17.
 */

public class Version implements Serializable {
    private String versionCode;
    private String file_real_path;
    private String file_MD5;
    private String min_version;
    private String version_explain;
    private String serviceIP;

    public String getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getFile_real_path() {
        return file_real_path;
    }

    public void setFile_real_path(String file_real_path) {
        this.file_real_path = file_real_path;
    }

    public String getFile_MD5() {
        return file_MD5;
    }

    public void setFile_MD5(String file_MD5) {
        this.file_MD5 = file_MD5;
    }

    public String getMin_version() {
        return min_version;
    }

    public void setMin_version(String min_version) {
        this.min_version = min_version;
    }

    public String getVersion_explain() {
        return version_explain;
    }

    public void setVersion_explain(String version_explain) {
        this.version_explain = version_explain;
    }
}
