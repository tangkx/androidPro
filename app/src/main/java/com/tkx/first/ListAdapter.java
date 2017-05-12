package com.tkx.first;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tkx.entiys.SimulateData;


import java.util.HashMap;
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
    private Integer index = -1;


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
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.listitem, null);
            viewHolder.addr = (TextView) view.findViewById(R.id.addrtext);
            viewHolder.number = (EditText) view.findViewById(R.id.numbertext);
            viewHolder.number.setFilters(new InputFilter[]{inputFilter,new InputFilter.LengthFilter(2)});

            viewHolder.number.setTag(position);
            viewHolder.number.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ((ViewGroup) v.getParent())
                            .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = (Integer) v.getTag();
//                        Log.i("0302", "onTouch"+index);
                    }
                    return false;
                }
            });


            class MyTextWatcher implements TextWatcher {

                private ViewHolder mHolder;

                public MyTextWatcher(ViewHolder holder) {
//            Log.i("0302", "构造函数TextWatcher");
                    mHolder = holder;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(context,"onTextChanged:"+s,Toast.LENGTH_LONG).show();

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    if (s.length() == 0) {

                        int position = (Integer) mHolder.number.getTag();
                        Log.e("老铁出错了:", "");
                        listdata.get(position).setNumber("00");

                    }else if(s.length() == 1){

                        int position = (Integer) mHolder.number.getTag();
                        Log.e("老铁出错了:", "");
                        listdata.get(position).setNumber("0"+s.toString());
                    }
                    else{

                        int position = (Integer) mHolder.number.getTag();
                        Log.e( "EditText的标记:","");
                        listdata.get(position).setNumber(s.toString());
                    }

                    // Toast.makeText(context,"afterTextChanged:"+s,Toast.LENGTH_LONG).show();
                }
            }

            viewHolder.number.addTextChangedListener(new MyTextWatcher(viewHolder));

            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();
            viewHolder.number.setTag(position);
        }


//        viewHolder.number.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ((ViewGroup) v.getParent())
//                        .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//                return false;
//            }
//        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((ViewGroup) v)
                        .setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                Intent intent = new Intent();
                intent.setAction("com.broadcast");
                intent.putExtra("position",position);
                context.sendBroadcast(intent);

//                setAnimationItem(position);
//                setAnimationItem(position+1);

                return false;
            }
        });



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


        return view;
    }

    public class ViewHolder {
        TextView addr;
        EditText number;

    }

    //文本字符过滤
    InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            String regex = "^[0-9a-fA-F]+$";

            if(!(source.toString().matches(regex))){
                return "";
            }
            return null;
        }
    };


}
