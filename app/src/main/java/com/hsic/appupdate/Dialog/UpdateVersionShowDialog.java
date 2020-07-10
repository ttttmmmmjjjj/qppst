package com.hsic.appupdate.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.appupdate.bean.Version;
import com.hsic.appupdate.net.INetDownloadCallBack;
import com.hsic.appupdate.updater.AppUpdater;
import com.hsic.appupdate.utils.AppUtils;
import com.hsic.tmj.qppst.R;

import java.io.File;

/**
 * 弹窗
 */
public class UpdateVersionShowDialog extends DialogFragment {

    private static final String TAG = "UpdateVersionShowDialog";

    private Version downloadBean ;

    private TextView title ,content,update,cansel;
    private Context mContext;
    private long versionCode;
    public static void show(FragmentActivity fragmentActivity, Version downloadBean)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable("download_bean",downloadBean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(fragmentActivity.getSupportFragmentManager(),TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
        Bundle bundle = getArguments();
        downloadBean = (Version) bundle.getSerializable("download_bean");
        versionCode= AppUtils.getVersionCode(mContext);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        initEvent();
    }

    private void initView(View view )
    {
        title = view.findViewById(R.id.title);
        content = view.findViewById(R.id.content);
        update = view.findViewById(R.id.update);
        cansel=view.findViewById(R.id.cansel);
        title.setText("版本更新");
        content.setText("版本说明："+downloadBean.getVersion_explain());
        String minVersion=downloadBean.getMin_version();
        if(!minVersion.equals("")){
            long tmp=Long.parseLong(minVersion);
            if(tmp>versionCode){
                cansel.setVisibility(View.GONE);
                title.setText("强制版本更新");
                content.setText("版本说明："+downloadBean.getVersion_explain());
            }
        }
    }
    private void initEvent()
    {
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        File targetFile = new File(getActivity().getCacheDir(),"update.apk");
                        AppUpdater.getInstance().getNetManager().download(downloadBean.getFile_real_path(), targetFile, new INetDownloadCallBack() {
                            @Override
                            public void success(File apkFile) {
                                dismiss();
                                AppUtils.installApk(getActivity(),apkFile.getPath());
                            }

                            @Override
                            public void failed(String throwable) {
                                final String info =throwable;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dismiss();
                            }
                            @Override
                            public void progress(final String progress) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        content.setText(progress);
                                    }
                                });

                            }
                        },UpdateVersionShowDialog.this,downloadBean.getFile_MD5());
                    }
                }).start();

            }
        });
        cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        AppUpdater.getInstance().getNetManager().cancel(UpdateVersionShowDialog.this);
    }
}

