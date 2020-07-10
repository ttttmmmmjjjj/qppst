package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hsic.bean.Count;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2018/11/28.
 */

public class CountAdapter extends BaseAdapter {
    private Context mContext;
    private List<Count> data=null;
    public CountAdapter(Context context, List<Count> list){
        this.mContext = context;
        this.data = list;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        if (data != null) {
            return data.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        if (data != null && data.size() > 0) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_count, null);
            TextView tvTitle= (TextView) convertView.findViewById(R.id.tvTitle);
            TextView tvValue= (TextView) convertView.findViewById(R.id.tvValue);
            tvTitle.setText(data.get(position).getName());
            tvValue.setText(data.get(position).getValue());
            return convertView;
        }else
            return null;

    }
}
