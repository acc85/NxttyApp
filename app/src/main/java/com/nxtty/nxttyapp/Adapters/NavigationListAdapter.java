package com.nxtty.nxttyapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nxtty.nxttyapp.Fragments.NavigationDrawerFragment;
import com.nxtty.nxttyapp.MainActivity;
import com.nxtty.nxttyapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raymond on 02/03/2015.
 */
public class NavigationListAdapter extends RecyclerView.Adapter<NavigationListAdapter.ViewHolder> {


    private Activity activity;
    private ArrayList<String> itemList;
    private NavigationDrawerFragment navigationDrawerFragment;


    public NavigationListAdapter(Activity activity, ArrayList<String> itemList, NavigationDrawerFragment navigationDrawerFragment){
        this.activity = activity;
        this.itemList = itemList;
        this.navigationDrawerFragment = navigationDrawerFragment;
    }

    @Override
    public NavigationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item_layout, null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String item = itemList.get(position);

        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_public_chat_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.navigation_drawer_public_chat_icon));
        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_open_chat_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.navigation_drawer_open_chat_icon));
        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_contacts_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.navigation_drawer_contacts_icon));
        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_find_users_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.navigation_drawer_find_users_icon));
        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_wallet_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.navigation_drawer_wallet_icon));
        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_settings_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.navigation_drawer_settings_icon));
        if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_logout_string).toString()))
            holder.icon.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_day));

        holder.title.setText(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.equalsIgnoreCase(activity.getResources().getText(R.string.navigation_logout_string).toString())){
                    new AlertDialog.Builder(activity).setMessage("are you sure you want to logout?")
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    navigationDrawerFragment.setOption(item);
                                    navigationDrawerFragment.selectOption(position);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }else {
                    navigationDrawerFragment.setOption(item);
                    navigationDrawerFragment.selectOption(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView icon;
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.item_icon);
            title = (TextView)itemView.findViewById(R.id.item_name);
        }
    }

}
