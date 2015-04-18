package com.example.androiddj.youtubeparser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.androiddj.R;

/**
 *
 * Created by Ashish Singh on 06-Apr-15.
 */
public class YoutubeDialogFragment extends DialogFragment {
    public static final String videoId = "videoId";
    public String url;
    Activity mActivity;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Resources res = getActivity().getResources();
        final Bundle bundle = getArguments();
        final String VIDEO_ID =bundle.getString(videoId);
        final String TITLE = bundle.getString("Description");
        url="https://www.youtube.com/watch?v="+VIDEO_ID;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.youtube_dialog_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which==1){
                            //play the video in youtube app
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                            startActivity(browserIntent);



//                            SEND the VIDEO_ID and title to the host

                        }
                        else if(which==0){
                            ListViewWithBaseAdapter.play(VIDEO_ID);
                        }
                        dialog.dismiss();
                    }
            })
            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK, so save the mSelectedItems results somewhere
                    // or return them to the component that opened the dialog
                    dialog.dismiss();
                }
            });
        return builder.create();
    }

    @Override
    public  void onAttach(Activity activity){
        super.onAttach(activity);
        mActivity=activity;
    }
}