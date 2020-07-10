package com.hsic.tmj.qppst;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.hsic.picture.ImageAdapter;
import com.hsic.utils.PathUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * 安检照片
 */
public class InsepctionPreviewActivity extends AppCompatActivity {
    private ArrayList<String> mList;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    String saleId = "", userID = "",relationID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent i=getIntent();
        saleId=i.getStringExtra("SaleID");
        userID=i.getStringExtra("UserID");
        relationID=i.getStringExtra("RelationID");
        gridView = (GridView) findViewById(R.id.gridView1);//
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent in = new Intent(InsepctionPreviewActivity.this, IBigPreViewActivity.class);
                in.putExtra("pathName", parent.getItemAtPosition(position).toString());
                in.putExtra("SaleID",saleId);
                in.putExtra("RelationID",relationID);
                in.putExtra("UserID",userID);
                startActivityForResult(in, 1);
            }
        });
        initData();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }
    /**
     *
     */
    private void initData() {
        mList = new ArrayList<String>();
        String filePath= PathUtil.getImagePath();
        File file = new File(filePath);
        String[] paths = file.list();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                String path = filePath+ paths[i];
                if (path.contains(relationID)) {
                    mList.add(path);
                }
            }

        }
        imageAdapter = new ImageAdapter(this, mList);//
        gridView.setAdapter(imageAdapter);//
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


}
