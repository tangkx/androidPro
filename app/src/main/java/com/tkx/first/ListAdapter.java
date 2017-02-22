package com.tkx.first;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class ListAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<SimulateData> listdata;

    public ListAdapter(Context context, List<SimulateData> listdata){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listdata = listdata;
        for(int i = 0; i < listdata.size(); i++){
            SimulateData sd = listdata.get(i);
            Log.d("addr:",sd.getAddr());
            Log.d("number:",sd.getNumber());
        }
    }
    @Override
    public int getCount() {
        return listdata.size();
    }

    @Override
    public Object getItem(int position) {
        return listdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.listitem, null);
            viewHolder.addr = (TextView) view.findViewById(R.id.addrtext);
            viewHolder.number = (EditText) view.findViewById(R.id.numbertext);

            view.setTag(viewHolder);

        }else{

            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.addr.setText(listdata.get(position).getAddr());
        viewHolder.number.setText(listdata.get(position).getNumber());

        return view;
    }

    public class ViewHolder{
        TextView addr;
        EditText number;
    }
}
