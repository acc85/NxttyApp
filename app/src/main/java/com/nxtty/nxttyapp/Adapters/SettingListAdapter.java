package com.nxtty.nxttyapp.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.Fragments.ProfileFragment;
import com.nxtty.nxttyapp.R;

import java.util.ArrayList;

/**
 * Created by Raymond on 04/03/2015.
 */
public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.ViewHolder> {


    public String[] items;
    public Activity activity;


    public SettingListAdapter(Activity activity){
        this.activity = activity;
        this.items = activity.getResources().getStringArray(R.array.settings_list);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item_layout,null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.itemName.setText(items[position]);
        //order: profile
        switch(position){
            case 0: {
                holder.itemIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_account));
                holder.itemView.setOnClickListener(
                        new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               activity.getFragmentManager().beginTransaction()
                                       .add(R.id.container,new ProfileFragment(), Constants.PROFILEFRAGMENT)
                                       .addToBackStack(Constants.PROFILEFRAGMENT)
                                       .commit();
                           }
                       });
            }
        }


    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemName;
        public ImageView itemIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView)itemView.findViewById(R.id.item_name);
            itemIcon = (ImageView)itemView.findViewById(R.id.item_icon);
        }
    }
}
