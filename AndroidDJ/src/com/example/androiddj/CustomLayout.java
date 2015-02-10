package com.example.androiddj;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Vipin on 10-Feb-15.
 */
public class CustomLayout extends RelativeLayout {

    TextView song;
    ImageButton upVote;
    ImageButton downVote;
    static TextView oldSong=null;
    static String initialValue="";

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, String song_text) {
        super(context);
        int baseID = 0;
        LayoutParams lp;

        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.height = 100;
        setPadding(0, 20, 0, 20);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(lp);

        song = new TextView(context);
        upVote = new ImageButton(context);
        downVote = new ImageButton(context);

        song.setId(baseID + 1);
        upVote.setId(baseID + 2);
        downVote.setId(baseID + 3);

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.leftMargin=10;
        song.setText(song_text);
        song.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        song.setTextSize(15);
        song.setTextColor(Color.DKGRAY);
        addView(song, lp);

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        downVote.setBackground(getResources().getDrawable(R.drawable.dislike));
        lp.height = 40;
        lp.width = 40;
        lp.rightMargin = 20;
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(downVote, lp);

        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        upVote.setBackground(getResources().getDrawable(R.drawable.like));
        lp.height = 40;
        lp.width = 40;
        lp.rightMargin = 30;
        lp.addRule(RelativeLayout.LEFT_OF, downVote.getId());
        addView(upVote, lp);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (oldSong != null) {
                    oldSong.setText(initialValue);
                }

                oldSong = song;
                initialValue = (String) oldSong.getText();
                oldSong.setText(initialValue + " is selected");
            }
        });
    }
}
