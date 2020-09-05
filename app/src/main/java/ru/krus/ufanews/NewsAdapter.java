package ru.krus.ufanews;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<News> objects;

    NewsAdapter(Context context, ArrayList<News> news){
        ctx = context;
        objects = news;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = lInflater.inflate(R.layout.item, parent, false);
        }
        News n = getNews(position);
        ((TextView) view.findViewById(R.id.row_tv_title)).setText(n.title);
        ((TextView) view.findViewById(R.id.row_tv_additional)).setText(n.additional);
        Picasso.get().load(Uri.parse(n.image)).into((ImageView)view.findViewById(R.id.row_img));
        return view;
    }

    News getNews(int position) {
        return ((News) getItem(position));
    }
}
