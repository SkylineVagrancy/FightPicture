package com.zjp.fightpicture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zjp.fightpicture.Bean.Relation;

import java.text.SimpleDateFormat;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by pzh on 2017/2/14.
 */

public class ScanImageUI extends AppCompatActivity {
    private String picUrl;
    private int position;
    private String defaultStr;
    private ImageView photoView;

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
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });


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

//    public Bitmap drawable2Bitmap(Drawable drawable) {
//        Bitmap bitmap = Bitmap
//                .createBitmap(
//                        drawable.getIntrinsicWidth(),
//                        drawable.getIntrinsicHeight(),
//                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                                : Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        return bitmap;
//    }
//
//    public byte[] Bitmap2Bytes(Bitmap bm) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        return baos.toByteArray();
//    }
//
//    // Drawable转换成byte[]
//    public byte[] Drawable2Bytes(Drawable d) {
//        Bitmap bitmap = this.drawable2Bitmap(d);
//        return this.Bitmap2Bytes(bitmap);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
