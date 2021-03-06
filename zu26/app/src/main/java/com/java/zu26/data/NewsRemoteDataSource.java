package com.java.zu26.data;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.util.Util;
import com.java.zu26.util.NewsDataUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    /*for (News news: newsList) {
                        if (news.getPictures().isEmpty()) {
                            news.setPictures(NewsDataUtil.find_image(news.getTitle()));
                            Log.d("PICTURE", "find picture" + news.getPictures());
                        }
                    }*/
                    callback.onNewsListLoaded(newsList);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "getUrlContent failed");
                }
            }
        }.start();
    }

    @Override
    public void getNewsList(int page, int category, @NonNull LoadNewsListCallback callback, boolean reverse) {

    }

    @Override
    public void getNews(@NonNull final String newsId, @NonNull boolean isDetailed, @NonNull final GetNewsCallback callback) {
        new Thread() {
            @Override
            public void run(){
                StringBuilder content = new StringBuilder();
                try {
                    URL url = new URL("http://166.111.68.66:2042/news/action/query/detail?newsId=" + newsId);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line + "\n");
                    }
                    bufferedReader.close();
                    News news = NewsDataUtil.parseNewsDetail(content.toString());
                    Log.d("remote", "get news detail from remote " + news.getTitle());
                    callback.onNewsLoaded(news);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "getUrlContent failed");
                }
            }
        }.start();
    }

    @Override
    public void getFavoriteNewsList(int page, @NonNull LoadNewsListCallback callback) {

    }

    @Override
    public void readNews(@NonNull String newsId) {

    }

    @Override
    public void favoriteNews(@NonNull News news) {

    }


    @Override
    public void unfavoriteNews(@NonNull String newsId) {

    }

    @Override
    public void saveNewsList(@NonNull ArrayList<News> newsList) {

    }

    @Override
    public void saveNewsList(@NonNull ArrayList<News> newsList, boolean recommend) {

    }

    @Override
    public void saveNews(@NonNull News news) {

    }

    @Override
    public void updateNewsDetail(@NonNull News news) {

    }

    @Override
    public void updateNewsPicture(@NonNull News news) {

    }

    @Override
    public void searchNews(final String keyWord, final int page, @NonNull final LoadNewsListCallback callback) {
        new Thread() {
            @Override
            public void run(){
                StringBuilder content = new StringBuilder();
                try {

                    URL url = new URL("http://166.111.68.66:2042/news/action/query/search?pageNo=" + String.valueOf(page) + "&pageSize=10&keyword=" + keyWord);
                    Log.d("remote", "search url: " + url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line + "\n");
                    }
                    bufferedReader.close();
                    Log.d("remote", "search: " + keyWord);
                    Log.d("remot", "parse: " + content.toString());
                    ArrayList<News> newsList = NewsDataUtil.parseLastedNewsListJson(content.toString());
                    callback.onNewsListLoaded(newsList);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "search news failed");
                }
            }
        }.start();
    }

    @Override
    public void searchKeywordNews(final String keyWord, final int count, @NonNull final LoadNewsListCallback callback, final HashSet<String> cache) {
        Log.d("", "searchKeywordNews: ");
        new Thread() {
            @Override
            public void run(){
                StringBuilder content = new StringBuilder();
                try {

                    URL url = new URL("http://166.111.68.66:2042/news/action/query/search?pageNo=1&pageSize=40&keyword=" + keyWord);
                    Log.d("remote", "search url: " + url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line + "\n");
                    }
                    bufferedReader.close();
                    Log.d("remote", "search: " + keyWord);
                    ArrayList<News> newsList = NewsDataUtil.parseLastedNewsListJson(content.toString());
                    ArrayList<News> retList = new ArrayList<News>();
                    int j = 0;
                    Set<String> titleSet = new HashSet<String>();
                    for (int i = 0; i < newsList.size() && j < count; i++) {
                        String title = newsList.get(i).getTitle();
                        if (!cache.contains(title) && !titleSet.contains(title)) {
                            j++;
                            titleSet.add(title);
                            retList.add(newsList.get(i));
                        }
                    }
                    callback.onNewsListLoaded(retList);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "search news failed");
                }
            }
        }.start();
    }

    @Override
    public void getCoverPicture(final News news, @NonNull final GetPictureCallback callback) {
        Log.d("PICTURE", "search in remote: " + news.getTitle());
        new Thread() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://image.baidu.com/search/index?tn=baiduimage&fm=result&ie=utf-8&word=" + news.getTitle());
                    Log.d("PICTURE", "search in remote thread: " + news.getTitle() + " " + url.toString());
                    InputStream in = url.openStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader bufr = new BufferedReader(isr);
                    String str, html = "";
                    String a = "\"hoverURL\":\"http:.+?\\.(jpg|jpeg|png|PNG)";
                    Pattern p = Pattern.compile(a);
                    while ((str = bufr.readLine()) != null) {
                        html = html + str;
                        Matcher m = p.matcher(str);
                        while (m.find()) {
                            bufr.close();
                            isr.close();
                            in.close();
                            Log.d("PICTURE", "found in remote: " + news.getTitle() + " " + m.group().substring(12));
                            callback.onPictureLoaded(m.group().substring(12));
                            return;
                        }
                    }
                bufr.close();
                isr.close();
                in.close();
                    Log.d("PICTURE", "not found in remote: " + news.getTitle());
                    Log.d("PICTURE", html);
                }

                catch(Exception e) {
                    Log.d("PICTURE", "exception: ");
                }
                finally {

                    callback.onPictureNotAvailable();

                    return;
                }
            }

        }.start();
    }
}
