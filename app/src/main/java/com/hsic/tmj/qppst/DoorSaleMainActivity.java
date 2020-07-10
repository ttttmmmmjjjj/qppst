package com.hsic.tmj.qppst;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.hsic.adapter.MenuGridAdapter;
import com.hsic.tmj.gridview.MyGridView;

/**
 * 门售主菜单
 */
public class DoorSaleMainActivity extends AppCompatActivity {
    private MyGridView gridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_sale_main);
        innitView();
    }
    private void innitView(){
        gridview = (MyGridView) findViewById(R.id.gridview);
        String[] img_text = { "下单", "提货", "作废", "统计", "补交打印","换货",};
        int[] imgs = { R.drawable.a, R.drawable.iocn_dollar,
                R.drawable.xd, R.drawable.count,
                R.drawable.b, R.drawable.g};
        gridview.setAdapter(new MenuGridAdapter(this,img_text,imgs));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                switch ((int) id) {
                    case 0://
                        Intent p=new Intent(DoorSaleMainActivity.this,PlaceOrderActivity.class);
                        startActivity(p);
                        break;
                    case 1:
                        Intent pi=new Intent(DoorSaleMainActivity.this,PickGoodsActivity.class);
                        startActivity(pi);
                        break;
                    case 2:
                        Intent c=new Intent(DoorSaleMainActivity.this,CanselOrderActivity.class);
                        startActivity(c);
                        break;
                    case 3:
//                        new GetSaleCountTask(MainActivity.this,MainActivity.this).execute();
                        break;
                    case 4:
                        Intent dp=new Intent(DoorSaleMainActivity.this,DelayPrintActivity.class);
                        startActivity(dp);
                        break;
                    case 5:
                        Intent ex=new Intent(DoorSaleMainActivity.this,ExchangeGoodsActivity.class);
                        startActivity(ex);
                        break;
                }
            }
        });
    }
}
