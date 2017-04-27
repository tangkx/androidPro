package com.tkx.first;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.tkx.entiys.SimulateData;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class ListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<SimulateData> listdata;
    private int ckeckItem = -1;
    private int animationItem = 0;

    public void animationCheck(int position) {
        ckeckItem = position;
        notifyDataSetChanged();
    }

    public void setAnimationItem(int position) {
        animationItem = position;
        notifyDataSetChanged();
    }

    public ListAdapter(Context context, List<SimulateData> listdata) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listdata = listdata;

    }

    public void refresh(List<SimulateData> listdata) {
        this.listdata = listdata;
        this.notifyDataSetChanged();
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
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.listitem, null);
            viewHolder.addr = (TextView) view.findViewById(R.id.addrtext);
            viewHolder.number = (EditText) view.findViewById(R.id.numbertext);

            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();
        }


        String addr = listdata.get(position).getAddr();
        String number = listdata.get(position).getNumber();
        viewHolder.addr.setText(addr);
        viewHolder.number.setText(number);

        if (animationItem == position || animationItem == (position + 1)) {

            viewHolder.addr.setBackground(context.getResources().getDrawable(R.drawable.textanimationshape));
            viewHolder.number.setBackground(context.getResources().getDrawable(R.drawable.textanimationshape));

        } else if (ckeckItem == position || ckeckItem == (position + 1)) {
            viewHolder.addr.setBackground(context.getResources().getDrawable(R.drawable.textselectshape));
            viewHolder.number.setBackground(context.getResources().getDrawable(R.drawable.textselectshape));

        } else {
            viewHolder.addr.setBackground(context.getResources().getDrawable(R.drawable.textshape));
            viewHolder.number.setBackground(context.getResources().getDrawable(R.drawable.textshape));
        }

//        if(ckeckItem == position || ckeckItem == (position + 1)){
//            viewHolder.addr.setBackground(context.getResources().getDrawable(R.drawable.textselectshape));
//            viewHolder.number.setBackground(context.getResources().getDrawable(R.drawable.textselectshape));
//
//        } else{
//            viewHolder.addr.setBackground(context.getResources().getDrawable(R.drawable.textshape));
//            viewHolder.number.setBackground(context.getResources().getDrawable(R.drawable.textshape));
//        }


//        viewHolder.addr.setBackground(context.getResources().getDrawable(R.drawable.textselectshape));
//        viewHolder.number.setBackground(context.getResources().getDrawable(R.drawable.textselectshape));

        return view;
    }

    public class ViewHolder {
        TextView addr;
        EditText number;
    }


}
