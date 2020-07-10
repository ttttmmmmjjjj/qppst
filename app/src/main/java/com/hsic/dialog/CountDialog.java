package com.hsic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hsic.adapter.CountAdapter;
import com.hsic.bean.Count;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2019/4/10.
 */

public class CountDialog extends Dialog implements View.OnClickListener {
    private ListView contentList;
    private TextView titleTxt;
    private TextView submitTxt;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;
    List<Count> countData;

    public CountDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CountDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public CountDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
        this.listener = listener;
    }
    public CountDialog(Context context, int themeResId, List<Count> countData, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.countData = countData;
        this.listener = listener;
    }
    protected CountDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public CountDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public CountDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CountDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_count);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        contentList = (ListView)findViewById(R.id.content);
        titleTxt = (TextView)findViewById(R.id.title);
        titleTxt.setText(title);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        contentList.setAdapter(new CountAdapter(mContext,countData));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.cancel:
//                if(listener != null){
//                    listener.onClick(this, false);
//                }
//                this.dismiss();
//                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this, true);
                }
                this.dismiss();
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
