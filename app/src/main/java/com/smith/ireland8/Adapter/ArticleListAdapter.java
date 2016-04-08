package com.smith.ireland8.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.smith.ireland8.Controller.AppController;
import com.smith.ireland8.Model.Article;
import com.smith.ireland8.Utils.ArticleImageView;

import java.util.List;

import com.smith.ireland8.R;

/**
 * Created by frank on 15-7-29.
 */
public class ArticleListAdapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Article> articles;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ArticleListAdapter(Activity activity, List<Article> articles) {
        this.activity = activity;
        this.articles = articles;
    }
    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int location) {
        return articles.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.article, null);
        }

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        Article article = articles.get(position);

        ArticleImageView articleImageView = (ArticleImageView) convertView
                .findViewById(R.id.articleImage);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(article.getTitle());

        TextView update_time = (TextView) convertView.findViewById(R.id.update_time);
        update_time.setText(article.getUpdate_time());

//		article image
        if (article.getThumb_url() != null) {
            articleImageView.setImageUrl(article.getThumb_url(), imageLoader);
            articleImageView.setVisibility(View.VISIBLE);
            articleImageView
                    .setResponseObserver(new ArticleImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            articleImageView.setVisibility(View.GONE);
        }
        return convertView;
    }

}
