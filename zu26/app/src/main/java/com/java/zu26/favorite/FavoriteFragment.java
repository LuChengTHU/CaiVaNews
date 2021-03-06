package com.java.zu26.favorite;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.zu26.R;
import com.java.zu26.data.News;
import com.java.zu26.newsList.NewsListActivity;
import com.java.zu26.newsPage.NewsPageActivity;
import com.java.zu26.util.ActivityUtils;
import com.java.zu26.util.UserSetting;

import java.util.ArrayList;

/**
 * Created by kaer on 2017/9/8.
 */

public class FavoriteFragment extends Fragment implements FavoriteContract.View{
    private Context mContext;

    private FavoriteContract.Presenter mPresenter;

    private FavoriteFragment.FavoriteAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;

    private int lastVisibleItem = 0;

    private int mPage = 0;


    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    //mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("favorite", "onCreateView: fragment");
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        mRecyclerView = root.findViewById(R.id.recyclerView_favorite);
        mContext = getContext();
        mAdapter = new FavoriteFragment.FavoriteAdapter(mContext, new ArrayList<News>(0));
        mRecyclerView.setAdapter(mAdapter);


        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);


        // 实现底部上拉刷新
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {


                    mPresenter.loadNews(mPage + 1, true);
                    //Toast.makeText(activity-context, "加载成功", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

            }
        });


//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//
//            }
//        });

        try {
            if (UserSetting.isDay(getContext())) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
        mPresenter.start();
    }

    @Override
    public void setPresenter(FavoritePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showNews(int page, ArrayList<News> newsList) {
        Log.d("TAG", "showNews: " + newsList.size());
        mAdapter.replaceData(newsList);
        mPage = page;
        mRecyclerView.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(0);
    }

    @Override
    public void showNoNews(int page, ArrayList<News> newslist) {

    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    private class FavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FavoriteItemView.onSlidingButtonListener{
        private LayoutInflater inflater;

        private ArrayList<News> newsList;

        private FavoriteItemView mItem = null;

        private Context context;

        public FavoriteAdapter(Context _context, ArrayList<News> _newsList) {
            this.inflater = LayoutInflater.from(_context);
            this.newsList = _newsList;
            this.context = _context;
        }

        public void replaceData(ArrayList<News> _newsList) {
            this.newsList.addAll(_newsList);
        }

        public News getItem(int i) {
            return newsList.get(i);

        }

        public void clearData() {
            Log.d("TAG", "onclearData:");
            if (newsList == null) {
                Log.d("TAG", "clearData: NULL");
            }
            else
                this.newsList.clear();
        }

        public void removeData(int pos) {
            mPresenter.removeFromFavorites(newsList.get(pos).getId());
            Log.d("delete", "removeData: " +newsList.get(pos).getTitle());
            this.newsList.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position <= getItemCount() )return 1;
            else return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 1) {

                final FavoriteItemView view = (FavoriteItemView) inflater.inflate(R.layout.favorite_itemlayout, parent, false);
                final ItemViewHolder holder = new ItemViewHolder(view);

                return holder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            Log.d("TAG", "onBindViewHolder: ");
            if(holder instanceof FavoriteFragment.ItemViewHolder) {

                News news = newsList.get(position);
                FavoriteFragment.ItemViewHolder itemHolder = (FavoriteFragment.ItemViewHolder)holder;
                itemHolder.newsTitle.setText(news.getTitle());
                //itemHolder.newsTime.setText(news.getTime());
                itemHolder.newsSource.setText(news.getSource());
                Log.d("TAG", "onBindViewHolder: " + position + news.getTitle());
                String url = news.getCoverPicture();
                try {
                    if (url != null && url.length() > 0) {
                        //Picasso.with(context).load(itemHolder.url).into(itemHolder.newsImage);
                        //itemHolder.newsImage.setImageBitmap(BitmapFactory.decodeStream(myurl.openStream()));
                        Glide.with(context).load(url).placeholder(R.drawable.downloading).into(itemHolder.newsImage);
                    }

                    else {
                        Glide.with(context).load(R.drawable.downloading).into(itemHolder.newsImage);
                    }

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                itemHolder.newsLayout.getLayoutParams().width = ActivityUtils.getScreenWidth(mContext);
                itemHolder.newsCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isItemOpen())
                            closeItem();
                        else {
                            Intent intent = new Intent();
                            intent.setClass(getContext(), NewsPageActivity.class);
                            News news = newsList.get(position);
                            newsList.set(position, new News(news, true, news.isFavorite()));
                            //Bundle bundle = new Bundle();
                            //bundle.putParcelable("news", mAdapter.getItem(position));
                            //bundle.putString("newsId", mAdapter.getItem(position).getId());
                            //intent.putExtras(bundle);
                            intent.putExtra("newsId", mAdapter.getItem(position).getId());
                            startActivity(intent);
                            Log.d("news list", "onClick: " + position + newsList.get(position).getTitle() + newsList.get(position).isRead());
                        }
                    }
                });
                itemHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeData(position);
                    }
                });
                itemHolder.itemView.setTag(position);
            }
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        @Override
        public void onMenuIsOpen(View view) {
            mItem = (FavoriteItemView) view;
        }

        @Override
        public void onDownOrMove(FavoriteItemView slidingButtonView) {
            if(isItemOpen()){
                if(mItem != slidingButtonView){
                    closeItem();
                }
            }
        }

        public boolean isItemOpen() {
            return mItem != null;
        }

        public void closeItem() {
            mItem.closeItem();
            mItem = null;
        }
        //void setOnItemClickListener(FavoriteFragment.OnItemClickListener listener) {
        //    itemListener = listener;
        //}
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView newsTime = null;
        public TextView newsTitle = null;
        public TextView newsSource = null;
        public TextView deleteBtn = null;
        public CardView newsCard = null;
        public ImageView newsImage = null;
        public ViewGroup newsLayout = null;

        public ItemViewHolder(View view) {
            super(view);
            newsTitle = (TextView) view.findViewById(R.id.favorite_news_title);
            newsSource = (TextView) view.findViewById(R.id.favorite_news_source);
            newsImage = (ImageView) view.findViewById(R.id.favorite_news_image);
            deleteBtn = (TextView) view.findViewById(R.id.favorite_delete);
            newsCard = (CardView) view.findViewById(R.id.favorite_card_view);
            newsLayout = (ViewGroup) view.findViewById(R.id.favorite_item_layout);
            ((FavoriteItemView) view).setSlidingButtonListener(mAdapter);
            newsTime = null;
        }
    }

    //public interface OnItemClickListener {
    //    void onItemClick(View view, int position);
    //}
}
