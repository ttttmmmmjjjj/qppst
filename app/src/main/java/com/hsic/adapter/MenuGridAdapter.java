package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hsic.tmj.qppst.R;


public class MenuGridAdapter extends BaseAdapter {
	private Context mContext;

    public String[] img_text = { "钢瓶管理", "门售", "车次管理", "订单管理", "灶位勘探","用户整改","查询统计","设置"};
    public int[] imgs = { };

    public MenuGridAdapter(Context mContext, String[] img_text, int[] imgs) {
        super();
        this.mContext = mContext;
        this.img_text=img_text;
        this.imgs=imgs;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);

        tv.setText(img_text[position]);
        return convertView;
    }
}
