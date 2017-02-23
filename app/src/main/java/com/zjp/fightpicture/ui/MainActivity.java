package com.zjp.fightpicture.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zjp.fightpicture.Bean.ImageBean;
import com.zjp.fightpicture.Bean.Tag;
import com.zjp.fightpicture.common.AppConfig;
import com.zjp.fightpicture.net.NetServer;
import com.zjp.fightpicture.R;
import com.zjp.fightpicture.Bean.ResultBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private SimpleAdapter adapter;
    private NetServer netServer;
    private int rn = 50;
    private int pn = 1000;
    private int count = 50;
    private ArrayList<String> datas = new ArrayList<>();
    private String defaulyStr = "表情包";
    private ArrayList<BmobObject> bmobImages = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_launcher);//设置ToolBar头部图标

        toolbar.setTitle("");//设置标题，也可以在xml中静态实现

        setSupportActionBar(toolbar);//使活动支持ToolBar

        Bmob.initialize(this, AppConfig.BMOB_ID);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mRecyclerView = (RecyclerView) findViewById(android.R.id.list);

        mSwipeRefreshWidget.setColorSchemeResources(R.color.orange, R.color.green,
                R.color.blue);
        mSwipeRefreshWidget.setOnRefreshListener(this);

        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeRefreshWidget.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {
                    mSwipeRefreshWidget.setRefreshing(true);
                    pn += count;
                    getData(rn, pn);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new ScrollListListener());

        adapter = new SimpleAdapter();
        mRecyclerView.setAdapter(adapter);
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl("http://www.baidu.com")
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        netServer = retrofit.create(NetServer.class);
        Glide.with(MainActivity.this).resumeRequests();
        getData(rn, pn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_search);//在菜单中找到对应控件的item
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                defaulyStr = query;
                Tag tag = new Tag();
                tag.setTagName(query);
                tag.setUpdateDate(format.format(Calendar.getInstance().getTime()));
                tag.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                    }
                });
                datas.clear();
                pn = 0;
                getData(rn, pn);
                menuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onRefresh() {
        datas.clear();
        pn = 0;
        getData(rn, pn);
    }

    public void getData(int rn, int pn) {
        mSwipeRefreshWidget.setRefreshing(true);
        netServer.getPicture(rn, pn, defaulyStr).enqueue(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                filterDatas(response.body());
                adapter.notifyDataSetChanged();
                mSwipeRefreshWidget.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                mSwipeRefreshWidget.setRefreshing(false);
            }
        });
    }


    public void filterDatas(ResultBean bean) {
        if (bean != null && bean.getImgs() != null) {
            for (ImageBean imageBean : bean.getImgs()) {
                String imageurl;
                if (!"".equals(imageBean.getObjURL())) {
                    imageurl = imageBean.getObjURL();
                } else if (!"".equals(imageBean.getHoverURL())) {
                    imageurl = imageBean.getHoverURL();
                } else if (!"".equals(imageBean.getLargeTnImageUrl())) {
                    imageurl = imageBean.getLargeTnImageUrl();
                } else if (!"".equals(imageBean.getMiddleURL())) {
                    imageurl = imageBean.getMiddleURL();
                } else {
                    imageurl = imageBean.getThumbURL();
                }
                datas.add(imageurl);
            }
        }
    }


    public class SimpleAdapter extends RecyclerView.Adapter<Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.image_item, null);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            Glide.with(MainActivity.this).load(datas.get(position))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ScanImageUI.class);
                    intent.putExtra("picUrl", datas.get(position));
                    intent.putStringArrayListExtra("datas", datas);
                    intent.putExtra("position", position);
                    intent.putExtra("defaulyStr", defaulyStr);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, holder.image, "sharedView").toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView image;

        public Holder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.pic);
        }
    }


    public class ScrollListListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case 2:
                    Glide.with(MainActivity.this).pauseRequests();
                    Log.d("TAG", "Picasso-----------暂停加载");
                    break;
                case 0:
                    Glide.with(MainActivity.this).resumeRequests();
                    Log.d("TAG", "Picasso-----------开始加载");
                    break;
                case 1:
                    Glide.with(MainActivity.this).resumeRequests();
                    Log.d("TAG", "Picasso-----------开始加载");
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(MainActivity.this).pauseRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(MainActivity.this).resumeRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(MainActivity.this).clearMemory();
    }
}
