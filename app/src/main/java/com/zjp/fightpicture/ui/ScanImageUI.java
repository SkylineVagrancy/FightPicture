package com.zjp.fightpicture.ui;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zjp.fightpicture.Bean.Relation;
import com.zjp.fightpicture.R;
import com.zjp.fightpicture.util.ShareUtils;

import java.text.SimpleDateFormat;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by pzh on 2017/2/14.
 */

public class ScanImageUI extends AppCompatActivity implements View.OnTouchListener{
    private String picUrl;
    private int position;
    private String defaultStr;
    private ImageView photoView;


    //放大缩小
    Matrix matrix=new Matrix();
    Matrix savedMatrix=new Matrix();

    PointF start=new PointF();
    PointF mid=new PointF();
    float oldDist;
    //模式
    static final int NONE=0;
    static final int DRAG=1;
    static final int ZOOM=2;
    int mode=NONE;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        photoView = (ImageView) findViewById(R.id.pic);
        getWindow().setExitTransition(new Transition() {
            @Override
            public void captureStartValues(TransitionValues transitionValues) {

            }

            @Override
            public void captureEndValues(TransitionValues transitionValues) {

            }
        });
        picUrl = getIntent().getStringExtra("picUrl");
        position = getIntent().getIntExtra("position", 0);
        defaultStr = getIntent().getStringExtra("defaultStr");
        Glide.with(ScanImageUI.this).load(picUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(photoView);
//        photoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finishAfterTransition();
//            }
//        });
        photoView.setOnTouchListener(this);


//        views = new SparseArray<>();
//        viewPager.setCurrentItem(position, true);
//        viewPager.setPageTransformer(true, new ZoomOutPagerTransformer());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Relation relation = new Relation();
                relation.setPictureUrl(picUrl);
                relation.setTagName(defaultStr);
                relation.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(ScanImageUI.this, "保存成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if (picUrl.endsWith("gif")) {
                    Glide.with(ScanImageUI.this).load(picUrl)
                            .asGif().toBytes().into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            ShareUtils.shareBitmapToWechat(ScanImageUI.this, 0, resource);
                            System.out.println("pzh onResourceReady");
                        }
                    });
                } else {
                    Glide.with(ScanImageUI.this).load(picUrl)
                            .asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                            ShareUtils.shareBitmapToWechat(ScanImageUI.this, 0, resource);
                            System.out.println("pzh onResourceReady");
                        }
                    });
                }

            }
        });


    }

//    public class ViewAdapter extends PagerAdapter {
//
//
//        @Override
//        public int getCount() {
//            return datas.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View view;
//            Glide.with(ScanImageUI.this).pauseRequests();
//            if (views.get(position) != null) {
//                view = views.get(position);
//            } else {
//                view = LayoutInflater.from(ScanImageUI.this).inflate(R.layout.view_pager_item, null);
//                views.put(position, view);
//            }
//            PhotoView imge = (PhotoView) view.findViewById(R.id.pic);
//            Glide.with(ScanImageUI.this).load(datas.get(position))
//                    .placeholder(R.drawable.loading)
//                    .priority(Priority.HIGH)
//                    .error(R.drawable.error)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(imge);
//            container.addView(view);
//            System.out.println("pzh instantiateItem position=" + position);
//            return view;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//
//        }
//    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch(event.getAction()&MotionEvent.ACTION_MASK){
            //设置拖拉模式
            case MotionEvent.ACTION_DOWN:
                matrix.set(photoView.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(),event.getY());
                mode=DRAG;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode=NONE;
                break;

            //设置多点触摸模式
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist=spacing(event);
                if(oldDist>10f){
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode=ZOOM;
                }
                break;
            //若为DRAG模式，则点击移动图片
            case MotionEvent.ACTION_MOVE:
                if(mode==DRAG){
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX()-start.x,event.getY()-start.y);
                }
                //若为ZOOM模式，则点击触摸缩放
                else if(mode==ZOOM){
                    float newDist=spacing(event);
                    if(newDist>10f){
                        matrix.set(savedMatrix);
                        float scale=newDist/oldDist;
                        //设置硕放比例和图片的中点位置
                        matrix.postScale(scale,scale, mid.x,mid.y);
                    }
                }
                break;
        }
        photoView.setImageMatrix(matrix);
        return true;
    }
    //计算移动距离
    private float spacing(MotionEvent event){
        float x=event.getX(0)-event.getX(1);
        float y=event.getY(0)-event.getY(1);
        return (float) Math.sqrt(x*x+y*y);
    }
    //计算中点位置
    private void midPoint(PointF point,MotionEvent event){
        float x=event.getX(0)+event.getX(1);
        float y=event.getY(0)+event.getY(1);
        point.set(x/2,y/2);
    }
}
