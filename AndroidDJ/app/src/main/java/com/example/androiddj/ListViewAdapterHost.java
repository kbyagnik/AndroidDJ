package com.example.androiddj;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListViewAdapterHost extends ArrayAdapter<Songs> {

    private List<Songs> StringList;
    private Context context;
    private int pos;
    public Button save;
    final String tag = "DJ Debugging";
    final HostView host;
    private static boolean flag = false;
    final DatabaseHandler db;
    private HashSet<Integer> songsUpvoted;
    private HashSet<Integer> songsDownvoted;


    public ListViewAdapterHost(List<Songs> StringList, Context ctx, int position, DatabaseHandler db) {
        super(ctx, R.layout.listview_content, StringList);
        Log.i(tag, "Inside list view adapter constructor");
        pos = position;
        this.StringList = StringList;
        this.context = ctx;
        this.host = (HostView) ctx;
        this.db = db;
        songsUpvoted = new HashSet<Integer>();
        songsDownvoted = new HashSet<Integer>();

    }

    public void setList(ArrayList<Songs> song) {
        StringList.clear();
        StringList.addAll(song);
        notifyDataSetChanged();
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i(tag, "Inside get view function");
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_content, parent, false);
        }

//        if (position==0) {
//            convertView.findViewById(R.id.table).setBackgroundColor(Color.GREEN);
//        }
        // Now we can fill the layout with the right values
        TextView name;
        Songs p;
        try {
            name = (TextView) convertView.findViewById(R.id.listViewItem);
            Log.d(tag,"size of adapter: "+this.getCount()+" size of list: "+StringList.size());
            p = StringList.get(position);
            Log.i("songdebug", "Song StringList- id " + Integer.toString(p.getID()) + "upvotes " + p.getUpvotes()+" link - "+p.get_url());

            p = db.getSong(p.getID());

        Log.i("songdebug", "Song db- id " + Integer.toString(p.getID()) + "upvotes " + p.getUpvotes()+" link - "+p.get_url());
        final int id = p.getID();
        name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        name.setSelected(true);
        name.setSingleLine(true);
        //String temp = p.getName();
          //  int index = temp.lastIndexOf("_");
            //Log.i("json1",db.getSong(1).getName() + " " + db.getSong(1).getUpvotes() + " " + db.getSong(1).getDownvotes());
        if(p.getName().lastIndexOf("_") == -1)
        {
            name.setText(p.getName());
        }
            else
        {
            //Log.i("newname",p.getName().substring(0,index));
            name.setText(p.getName().substring(0,p.getName().lastIndexOf("_")));
        }
        final TextView upvotes = (TextView) convertView.findViewById(R.id.upvoteCount);
        final TextView downvotes = (TextView) convertView.findViewById(R.id.downvoteCount);
        upvotes.setText(Integer.toString(p.getUpvotes()));
        downvotes.setText(Integer.toString(p.getDownvotes()));
        Log.i(tag, Integer.toString(position) + "    " + Integer.toString(pos));
        final Button upvote = (Button) convertView.findViewById(R.id.upvote);
        final Button downvote = (Button) convertView.findViewById(R.id.downvote);

        save = (Button)convertView.findViewById(R.id.saveSong);

        save.setEnabled(false);
        save.setVisibility(View.GONE);

        /*if (pos == position) {*/
        boolean upvoted = songsUpvoted.contains(id);
        boolean downvoted = songsDownvoted.contains(id);
        if (upvoted) {
            upvote.setAlpha((float)0.5);
            upvote.setEnabled(false);
            downvote.setEnabled(false);
            downvote.setAlpha((float)0.25);
        } else if (downvoted) {
            upvote.setEnabled(false);
            downvote.setEnabled(false);
            upvote.setAlpha((float)0.25);
            downvote.setAlpha((float)0.5);
        }
         else {
            upvote.setEnabled(true);
            downvote.setEnabled(true);
            upvote.setAlpha((float) 1.0);
            downvote.setAlpha((float) 1.0);
        }
        upvote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(tag, "inside on click");
//                upvote.setEnabled(false);
//                downvote.setEnabled(false);
                Songs song = db.getSong(id);
//                upvote.setAlpha((float)0.5);
//                downvote.setAlpha((float)0.25);
                Log.i(tag, "Got song with id " + Integer.toString(id) + " and name " + song.getName() + " upvotes " + song.getUpvotes());
                int upvotesCount = song.getUpvotes();
                song.setUpvotes(upvotesCount + 1);
                songsUpvoted.add(song.getID());
                Log.i(tag, "upvotes incremented");
                db.updateSong(id, song.getStatus(), song.getUpvotes(), song.getDownvotes(), song.getAging());
                Log.i(tag, "database updated " + " upvotes " + db.getSong(id).getUpvotes());
                upvotes.setText(Integer.toString(song.getUpvotes()));
                Log.i(tag, "upvotes shown");
                host.sortList();


            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Songs song = db.getSong(id);
//                upvote.setEnabled(false);
//                downvote.setEnabled(false);
//                upvote.setAlpha((float)0.5);
//                downvote.setAlpha((float)0.25);
                songsDownvoted.add(new Integer(song.getID()));
                int downvotesCount = song.getDownvotes();
                song.setDownvotes(downvotesCount + 1);
                db.updateSong(id, song.getStatus(), song.getUpvotes(), song.getDownvotes(), song.getAging());
                downvotes.setText(Integer.toString(song.getDownvotes()));
                host.sortList();
            }
        });

        /*for(int i=0;i<10;i++)
        {
			StringList.add("Song"+Integer.toString(i));
		}*/

        } catch (Exception e) {
            Log.i(tag, e.getMessage());
        }

        return convertView;
    }

    public void setPosition(int position) {
        pos = position;
    }
}