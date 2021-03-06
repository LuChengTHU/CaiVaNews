package com.java.zu26.newsPage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.java.zu26.R;
import com.java.zu26.data.News;
import com.java.zu26.data.NewsDataSource;
import com.java.zu26.data.NewsLocalDataSource;
import com.java.zu26.data.NewsRemoteDataSource;
import com.java.zu26.data.NewsRepository;
import com.java.zu26.favorite.FavoriteActivity;
import com.java.zu26.newsList.NewsListActivity;
import com.java.zu26.util.ActivityUtils;
import com.java.zu26.util.DayNight;
import com.java.zu26.util.SpeechUtils;
import com.java.zu26.util.UserSetting;

/**
 * Created by lucheng on 2017/9/6.
 */

public class NewsPageActivity extends AppCompatActivity {

    private NewsPagePresenter mPresenter;

    private Toolbar mToolbar;

    private Context mContext;

    private String mNewsId;

    private boolean mFavorite;

    private SpeechUtils mSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newspage);


        mContext = NewsPageActivity.this;
        NewsLocalDataSource newsLocalDataSource = NewsLocalDataSource.getInstance(mContext);
        NewsRemoteDataSource newsRemoteDataSource = NewsRemoteDataSource.getInstance();
        NewsRepository newsRepository = NewsRepository.getInstance(newsRemoteDataSource, newsLocalDataSource);
        //Bundle bundle = getIntent().getExtras();
        //mRawNews = bundle.getParcelable("news");
        mNewsId = getIntent().getStringExtra("newsId");
        Log.d("new page", "onCreate: newId = " + mNewsId);

        mFavorite = newsLocalDataSource.isFavorite(mNewsId);
        Log.d("News Page", "onCreate: favorite" + mFavorite);
        mToolbar = (Toolbar) findViewById(R.id.news_page_toolbar);

        NewsPageFragment newsPageFragment =
                (NewsPageFragment) getSupportFragmentManager().findFragmentById(R.id.page_frame);
        if (newsPageFragment == null) {
            // Create the fragment
            newsPageFragment = NewsPageFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), newsPageFragment, R.id.page_frame);
        }


        mPresenter = new NewsPagePresenter(newsRepository, newsPageFragment, mToolbar, this);


        //final boolean rawFavorite = mPresenter.getNews().isFavorite();

        mPresenter.getContext(mContext);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mToolbar.setNavigationIcon(R.mipmap.navigation);

        mSpeech = SpeechUtils.getsSpeechUtils(mContext);

        try {
            if (UserSetting.isDay(NewsPageActivity.this)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("", "onResume: presenter start");
        mPresenter.start(mNewsId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("", "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.menu_news_page, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mFavorite)
            mToolbar.getMenu().findItem(R.id.news_page_toolbar_favorite).setIcon(R.mipmap.favorite_true);
        else
            mToolbar.getMenu().findItem(R.id.news_page_toolbar_favorite).setIcon(R.mipmap.favorite);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            private boolean isReading = false;
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.news_page_toolbar_share:
                        mPresenter.showShareDialog();
                        break;
                    case R.id.news_page_toolbar_favorite:
                        if(mFavorite) {
                            mPresenter.removeFromFavorites();
                            Toast.makeText(mContext, "Removed from favorites", Toast.LENGTH_SHORT).show();
                            menuItem.setIcon(R.mipmap.favorite);
                        }
                        else {
                            mPresenter.addToFavorites();
                            Toast.makeText(mContext, "Added to favorites", Toast.LENGTH_SHORT).show();
                            menuItem.setIcon(R.mipmap.favorite_true);
                        }
                        mFavorite = !mFavorite;
                        break;
                    case R.id.news_page_toolbar_voice:
                        if(!isReading) {
                            isReading = true;
                            mPresenter.readText();
                            menuItem.setIcon(R.mipmap.voice_true);
                        }
                        else {
                            isReading = false;
                            mPresenter.stopReadingText();
                            menuItem.setIcon(R.mipmap.voice);
                        }
                }
                return true;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeech.stop();
        mSpeech.release();
    }

    public void prepareToolbar(final boolean rawFavorite) {
        /*
        if(rawFavorite) {
            if (mToolbar.getMenu() == null)
                Log.d("", "prepareToolbar: NO MENU");
            else {
                if (mToolbar.getMenu().findItem(R.id.news_page_toolbar_favorite) == null)
                    Log.d("", "prepareToolbar: NO ITEM");
            }
                //mToolbar.getMenu().findItem(R.id.news_page_toolbar_favorite).setIcon(R.mipmap.favorite_true);
        }


        */
    }


    public void readText(String text) {
        mSpeech.speak(text);
    }

    public void stopReadingText() {
        mSpeech.stop();
    }

}
