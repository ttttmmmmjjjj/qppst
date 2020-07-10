package com.hsic.permission;

/**
 * Created by Administrator on 2019/3/12.
 */

public interface PermissionInterface {
    public void requestPermissionsSuccess();
    public void requestPermissionsFail();
    public int getPermissionsRequestCode();
    public String[] getPermissions();
}
