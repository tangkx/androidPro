package com.tkx.first;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tkx on 2017/5/21.
 */

public class WelcomeActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private Button btn_skip, ed_time;
    private ViewPager viewPager;
    private int[] imageId = {R.mipmap.p1, R.mipmap.p2, R.mipmap.p3, R.mipmap.p4};
    private List<View> viewList;
    private ImageView[] points;
    private int currentIndex = 0;
    private View lastView;
    private Thread thread;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcomelayout);

        initView();
        thread = new Thread(skipRun);
        thread.start();
    }

    public void initView(){

        ed_time = (Button) findViewById(R.id.back_time);
        btn_skip = (Button) findViewById(R.id.to_main);
        btn_skip.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewList = new ArrayList<>();
        for (int i = 0; i < imageId.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);//设置缩放样式
            imageView.setImageResource(imageId[i]);
            viewList.add(imageView);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        points = new ImageView[4];
        //初始化布局中的小圆点ImageView控件
        for (int i = 0; i < points.length; i++) {
            points[i] = (ImageView) linearLayout.getChildAt(i);//遍历LinearLayout下的所有ImageView子节点
            points[i].setEnabled(true);//设置当前状态为允许点击（可点，灰色）
            points[i].setOnClickListener(this);//设置点击监听
            //额外设置一个标识符，以便点击小圆点时跳转对应页面
            points[i].setTag(i);//标识符与圆点顺序一致
        }
        currentIndex = 0;
        points[currentIndex].setEnabled(false);//设置首页为当前页(不可点，黑色)

    }

    public Runnable skipRun = new Runnable() {

        int time = 7;
        @Override
        public void run() {

            while (time >= 0 && flag){
                try{

                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = time;
                    handler.sendMessage(message);
                    Thread.sleep(1000);
                    time = time - 1;
                }catch (Exception e){

                }
            }
            Thread.interrupted();
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.to_main:
                flag = false;
                thread.interrupt();
                Intent intent = new Intent();
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case 1:
                    int time = (int) msg.obj;
                    ed_time.setText(time+"s");
                    if(time == 0){

                        Intent intent = new Intent();
                        intent.setClass(WelcomeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if(position < 4){
            points[position].setEnabled(false);//不可点击
            points[currentIndex].setEnabled(true);//恢复之前页面状态
            currentIndex = position;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class ViewPagerAdapter extends PagerAdapter{

        private List<View> list;
        public ViewPagerAdapter(List<View> list){
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View)list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)list.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
