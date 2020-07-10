package com.hsic.appupdate;

public interface NetCallBack {

    void success(String response);
    void failed(String throwable);

}
