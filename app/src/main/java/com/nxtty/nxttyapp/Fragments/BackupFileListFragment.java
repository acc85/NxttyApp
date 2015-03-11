package com.nxtty.nxttyapp.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nxtty.nxttyapp.Adapters.BackupFileListAdapter;
import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.StartActivity;
import com.nxtty.nxttyapp.Utilities;

import java.io.File;

/**
 * Created by Raymond on 19/02/2015.
 */
public class BackupFileListFragment extends Fragment {

    private RecyclerView.LayoutManager mLayoutManager;
    private View touchOverlay;
    private boolean listItemPressed;
    private BackupFileListAdapter backupFileListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View backupFileListLayout = inflater.inflate(R.layout.backup_file_list_layout,null);

        backupFileListAdapter = new BackupFileListAdapter(getActivity(),Utilities.getInstance().getBackupFileList());

        touchOverlay = backupFileListLayout.findViewById(R.id.backup_file_list_touch_overlay);

        touchOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //do nothing
            }
        });

        RecyclerView recyclerView = (RecyclerView)backupFileListLayout.findViewById(R.id.backup_file_list);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(backupFileListAdapter);

        return backupFileListLayout;
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit, enter, nextAnim);
        if(nextAnim != 0) {
            animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!enter) {
                        LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                        if (loginFragment != null)
                            try {
                                loginFragment.loginAnimateFadeIndAndScaleUp();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (enter) {
                        LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                        if (loginFragment != null)
                            loginFragment.setImportButtonPressed(false);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else{
            if(enter)
                animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.empty);
            else
                animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.slide_out_from_top_to_bottom);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!enter) {
                        LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                        if (loginFragment != null)
                            try {
                                loginFragment.loginAnimateFadeIndAndScaleUp();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (enter) {
                        LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                        if (loginFragment != null)
                            loginFragment.setImportButtonPressed(false);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return animator;
    }

    public void setListItemPressed(boolean bool){
        listItemPressed = bool;
    }

    public void showTouchOverlay(boolean visible){
        if(visible){
            touchOverlay.setVisibility(View.VISIBLE);
        }else{
            touchOverlay.setVisibility(View.GONE);
        }
    }

    public boolean isListItemPressed(){
        return listItemPressed;
    }



    public void showImportDialogFragment(File file){
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in_slide_in_from_right_to_left,R.animator.fade_out_scale_down,R.animator.fade_in_slide_in_from_left_to_right, R.animator.fade_out_slide_out_from_left_to_right)
                .add(R.id.start_activity_container, ImportDetailsFragment.builder(file),Constants.IMPORTDETAILSDIALOGFRAGMENT)
                .addToBackStack(Constants.IMPORTDETAILSDIALOGFRAGMENT)
                .commit();
    }

    public void backupListFragmentAnimateFadeIndAndScaleUp(){
        showTouchOverlay(false);
        getView().findViewById(R.id.backup_file_list_layout).setAlpha(0.5f);
        getView().findViewById(R.id.backup_file_list_layout).setScaleX(0.7f);
        getView().findViewById(R.id.backup_file_list_layout).setScaleY(0.7f);
        getView().findViewById(R.id.backup_file_list_layout).animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(150)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        backupFileListAdapter.repopulateFileList();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

    }


    public void animateAndShowImportDialogFragment(final File file){
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        showTouchOverlay(true);
        getView().findViewById(R.id.backup_file_list_layout).animate()
                .alpha(0.5f)
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                                 @Override
                                 public void onAnimationStart(Animator animation) {
                                     if (listItemPressed) {
                                         showImportDialogFragment(file);
                                     }
                                 }

                                 @Override
                                 public void onAnimationEnd(Animator animation) {

                                 }

                                 @Override
                                 public void onAnimationCancel(Animator animation) {

                                 }

                                 @Override
                                 public void onAnimationRepeat(Animator animation) {

                                 }
                             }
                ).start();
    }


    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.white_line);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
