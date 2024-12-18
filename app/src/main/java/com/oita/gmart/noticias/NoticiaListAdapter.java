package com.oita.gmart.noticias;

import android.content.Context;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by gmart on 08/01/2018.
 */

public class NoticiaListAdapter extends ArrayAdapter<Noticia> {

    private static final String TAG = "NoticiaListAdapter";

    private Context mContext;
    int mResource;

    public NoticiaListAdapter(Context context, int resource, ArrayList<Noticia> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //buscar as especificações das notícias
        String title = getItem(position).getTitle();
        String date = getItem(position).getDate();
        String content = getItem(position).getContent();
        String thumbnailUrl = getItem(position).getThumbnailUrl();

        //criar o objeto notícia com a informação
        Noticia noticia = new Noticia(title,date,content,thumbnailUrl);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView tvDate = (TextView) convertView.findViewById(R.id.txtPubDate);
        TextView tvContent = (TextView) convertView.findViewById(R.id.txtContent);
        TextView tvUrl = (TextView) convertView.findViewById(R.id.txtUrl);
        ImageView imagemFromUrl = (ImageView)convertView.findViewById(R.id.imageView);

        Picasso.get().load(thumbnailUrl).into(imagemFromUrl);

        tvTitle.setText(title);
        tvDate.setText(date);
        tvContent.setText(content);
        tvUrl.setText(thumbnailUrl);

        return convertView;
    }
}
