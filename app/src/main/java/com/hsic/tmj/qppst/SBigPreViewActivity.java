package com.hsic.tmj.qppst;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hsic.bll.GetBasicInfo;
import com.hsic.db.AJDB;

import java.io.File;

/**
 * Created by Administrator on 2019/3/14.
 */

public class SBigPreViewActivity  extends AppCompatActivity {
    private ImageView mImageview;
    private Bitmap decodeFile;
    String path="";
    String fileName="";
    String userId = "";
    GetBasicInfo getBasicInfo;
    AJDB ajdb;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_pre_view);
        mImageview = (ImageView) findViewById(R.id.bigpreview);
        getBasicInfo=new GetBasicInfo(this);
        ajdb=new AJDB(this);

    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Intent intent = getIntent();
        userId=intent.getStringExtra("UserID");
        path = intent.getStringExtra("pathName");
        fileName=path.substring(path.lastIndexOf("/")+1);
        Bitmap big = big(path);
        mImageview.setImageBitmap(big);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (decodeFile != null && !decodeFile.isRecycled()) {
            decodeFile.recycle();
            decodeFile = null;

        }
        System.gc();
    }

    /**
     *
     * @param path
     * @return
     */
    public Bitmap big(String path) {
        Bitmap resizeBmp = BitmapFactory.decodeFile(path);
        return resizeBmp;
    }

    /**
     *
     * @param v
     *
     */
    public void click(View v) {
        this.finish();
    }

    /**
     *
     * @param v
     */
    public void click1(View v) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            ajdb.DeleteAJAssociation(fileName, getBasicInfo.getOperationID(), userID);
        }
        this.finish();
    }
}
