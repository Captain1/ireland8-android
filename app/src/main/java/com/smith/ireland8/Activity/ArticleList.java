package com.smith.ireland8.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.smith.ireland8.Adapter.ArticleListAdapter;
import com.smith.ireland8.Controller.AppController;
import com.smith.ireland8.Model.Article;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smith.ireland8.R;
import com.smith.ireland8.Utils.OnSwipeTouchListener;


public class ArticleList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ArticleList.class.getSimpleName();
    private SwipeRefreshLayout mSwipeLayout;
    private ListView listView;
    private ArticleListAdapter listAdapter;
    private List<Article> articles;
    private String URL = "http://api.ireland8.com/news?page=";

    //    private RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_layer);
        initUI();
    }

    public void initUI() {
//        ActionBar ab = getSupportActionBar();
//        ab.setCustomView();

        //refresh and go to top
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //update and go to top
                refetch();
                listView.setSelectionAfterHeaderView();
            }
        });

        //pagination call listens on swipe action on listview
        listView = (ListView) findViewById(R.id.article_list);
        listView.setOnTouchListener(new OnSwipeTouchListener(ArticleList.this) {
            int count= 0;
            public void onSwipeRight() {
                count+=1;
                URL += count;
                refetch();
            }
            public void onSwipeLeft() {
                count-=1;
                URL += count;
                refetch();
            }
        });


        articles = new ArrayList<Article>();
        listAdapter = new ArticleListAdapter(this, articles);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(listView.getContext(), WebViewActivity.class);
                i.putExtra("url", articles.get(position).getUrl());
                startActivity(i);
            }
        });
/*        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/

        // We first check for cached request
        Entry entry = null;

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        entry = cache.get(URL);

        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonArticle(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            refetch();
        }
    }

    /**
     * Parsing json response and passing the data to article list adapter
     */
    private void parseJsonArticle(JSONObject response) {
        try {
            System.out.println(response);
            JSONArray arr = response.getJSONArray("articles");
            System.out.println(arr);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject articleJSON = (JSONObject) arr.get(i);

                Article article = new Article();
                //Image url might be null sometimes
                String image_url = articleJSON.isNull("thumb_url") ? null : articleJSON
                        .getString("thumb_url");
                article.setThumb_url(image_url);

                article.set_id(articleJSON.getString("_id"));
                article.setTitle(articleJSON.getString("title"));
                article.setAuthor(articleJSON.getString("author"));
                article.setUrl(articleJSON.getString("url"));
                article.setContent_source_url(articleJSON.getString("content_source_url"));
                article.setUpdate_time(articleJSON.getString("update_time"));

                articles.add(article);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refetch() {
        final int no_before = articles.size();
//        HashMap map = new HashMap();
//        map.put("parameters", "{\"page\":0,\"perpage\":50}");
//        JSONObject jsonObject = new JSONObject(map);

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq_article = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    articles.clear();
                    parseJsonArticle(response);
                    int no_after = articles.size();
                    Log.d("no2", (String.valueOf(no_after)));
                    Context context = getApplicationContext();
                    CharSequence text = (no_after - no_before) + " articles updated!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq_article);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu., menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRefresh() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                refetch();
            }
        }, 2000);
    }

    public void exit(MenuItem item) {
        finish();
    }

    // 连续两次返回退出程序
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Toast.makeText(getApplicationContext(), "退出", Toast.LENGTH_SHORT).show();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}