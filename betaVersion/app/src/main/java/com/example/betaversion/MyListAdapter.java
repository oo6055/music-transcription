package com.example.betaversion;
import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * the adapter for the custom list
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021  view the sections
 */
public class MyListAdapter extends BaseAdapter {

    private final Activity context;
    private ArrayList<Section> sections;

    /**
     * Instantiates a new My list adapter.
     *
     * @param context  the context
     * @param sections the sections
     */
    public MyListAdapter(Activity context, ArrayList<Section> sections) {
        // TODO Auto-generated constructor stub


        this.context=context;
        this.sections = sections;


    }

    /**
     * return the amount of sections.
     *
     * @return the amount of sections
     */
    @Override
    public int getCount() {
        return sections.size();
    }

    /**
     * return the item in specific position
     *
     * @param position the pos that we want to get
     * @return the amount of sections
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * return the item in specific position
     *
     * @param position
     * @param view
     * @param parent
     * @return the view that it present in the list
     */
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(this.sections.get(position).getNickName());
        if (this.sections.get(position).getPublicOrPrivate())
            imageView.setImageResource(R.drawable.publicpic);
        else
        {
            imageView.setImageResource(R.drawable.privateicon);
        }
        subtitleText.setText(this.sections.get(position).getDate());



        return rowView;

    }
}