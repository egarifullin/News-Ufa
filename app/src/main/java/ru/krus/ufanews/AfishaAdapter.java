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

public class AfishaAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Afisha> objects;

    AfishaAdapter(Context context, ArrayList<Afisha> afishas){
        ctx = context;
        objects = afishas;
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
            view = lInflater.inflate(R.layout.item_afisha, parent, false);
        }
        Afisha n = getAfisha(position);
        ((TextView) view.findViewById(R.id.tvArtist)).setText(n.artist);
        ((TextView) view.findViewById(R.id.placeArtist)).setText(n.place);
        ((TextView) view.findViewById(R.id.dateArtist)).setText(n.date);
        ((TextView) view.findViewById(R.id.priceArtist)).setText(n.price);
        Picasso.get().load(Uri.parse(n.image)).into((ImageView)view.findViewById(R.id.imgArtist));
        return view;
    }

    Afisha getAfisha(int position) {
        return ((Afisha) getItem(position));
    }
}
