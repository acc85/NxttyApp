package com.nxtty.nxttyapp.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.Fragments.BackupFileListFragment;
import com.nxtty.nxttyapp.Utilities;

/**
 * Created by Raymond on 19/02/2015.
 */
public class BackupFileListAdapter extends RecyclerView.Adapter<BackupFileListAdapter.ViewHolder> {


    ArrayList<File> fileList;

    private Activity activity;

    public BackupFileListAdapter(Activity activity, ArrayList<File> fileList){
        this.fileList = fileList;
        this.activity = activity;
    }


    @Override
    public BackupFileListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backup_file_list_row_layout,null);

        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    public void repopulateFileList(){
        this.fileList.clear();
        this.fileList.addAll(Utilities.getInstance().getBackupFileList());
        notifyDataSetChanged();
    }


    public void showImportDialogFragment(File file){
        BackupFileListFragment fragment = (BackupFileListFragment)activity.getFragmentManager().findFragmentByTag(Constants.BACKUPFILELISTFRAGMENT);
        if(!fragment.isListItemPressed()) {
            fragment.setListItemPressed(true);
            fragment.animateAndShowImportDialogFragment(file);
        }

    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final File file = fileList.get(i);
        viewHolder.textView.setText(file.getName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialogFragment(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            TextView textView = (TextView)itemView.findViewById(R.id.backup_file_list_file_name);
            this.textView = textView;
        }
    }
}
