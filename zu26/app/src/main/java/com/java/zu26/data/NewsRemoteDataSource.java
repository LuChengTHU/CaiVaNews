package com.java.zu26.data;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.java.zu26.util.NewsDataUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaer on 2017/9/5.
 */
public class NewsRemoteDataSource implements NewsDataSource {

    private static NewsRemoteDataSource INSTANCE;

//    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

//    private final static Map<String, Task> TASKS_SERVICE_DATA;

    /*
    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
    }
    */

    public static NewsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewsRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private NewsRemoteDataSource() {}
    /*
    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }
    */

    @Override
    public void getNewsList(final int page, final int category, @NonNull final LoadNewsListCallback callback) {
        new Thread() {
            @Override
            public void run(){
                StringBuilder content = new StringBuilder();
                try {
                    URL url = new URL("http://166.111.68.66:2042/news/action/query/latest?pageNo=" + String.valueOf(page) + "&pageSize=10&category=" + String.valueOf(category));

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line + "\n");
                    }
                    bufferedReader.close();
                    //Log.d("TAG", "run: ");
                    ArrayList<News> newsList = NewsDataUtil.parseLastedNewsListJson(content.toString());
                    callback.onNewsListLoaded(newsList);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "getUrlContent failed");
                }
            }
        }.start();
    }

    @Override
    public void getNews(@NonNull String newsId, @NonNull GetNewsCallback callback) {

    }

    @Override
    public void readNews(@NonNull String newsId) {

    }

    @Override
    public void favoriteNews(@NonNull String newsId) {

    }

    @Override
    public void unfavoriteNews(@NonNull String newsId) {

    }

    @Override
    public void saveNewsList(@NonNull ArrayList<News> newsList) {

    }

    @Override
    public void saveNews(@NonNull News news) {

    }


}