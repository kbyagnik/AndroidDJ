package com.example.androiddj;

import java.util.ArrayList;
import java.util.List;

import com.example.androiddj.database.DatabaseHandler;
import com.example.androiddj.database.Songs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/*class ListViewAdapter1
{
	final List<Songs> songs;
	final ListView list;
	final Context context;
	
	public ListViewAdapter1(ArrayList<Songs> songs,ListView list,Context context)
	{		
        this.songs = songs;
        this.list = list;
        this.context = context;
		CreateListView();
	}
	
	public void CreateListView()
    {
		for(int i=0;i<10;i++)
		{
			((View) songs).getParent().add("Song"+Integer.toString(i));
		}
		final int indexChanged[] = new int[1];
		final String initialValue[] = new String[1];
		indexChanged[0] = -1;
		initialValue[0] = "";
         list.setAdapter(new ArrayAdapter<Songs>(context, android.R.layout.simple_list_item_1,songs));
         list.setOnItemClickListener(new OnItemClickListener()
           {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position,long id)
                {
                    //args2 is the listViews Selected index
                	if(indexChanged[0] != -1)
                	{
                		songs.set(indexChanged[0], initialValue[0]);
                	}
                	indexChanged[0] = position;
                	initialValue[0] = songs.get(position);
                	//songs.set(position, songs.get(position) + " is selected");
                	view.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                      @Override
                      public void run() {
                        songs.set(position, songs.get(position) + " is selected");
                        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                      }
                    });
                }
           });
    }
	
}
*/

public class ListViewAdapterHost extends ArrayAdapter<Songs>
{
	 
private List<Songs> StringList;
private Context context;
private int pos;
final String tag = "DJ Debugging";
final HostView host;
final DatabaseHandler db;
private ArrayList<Integer> songsVotes;
 
public ListViewAdapterHost(List<Songs> StringList, Context ctx,int position,DatabaseHandler db) {
    super(ctx, R.layout.listview_content, StringList);
    Log.i(tag, "Inside list view adapter constructor");
    pos = position;
    this.StringList = StringList;
    this.context = ctx;
    this.host = (HostView) ctx;
    this.db = db;
    songsVotes = new ArrayList<Integer>();
}

    public void setList(ArrayList<Songs> songs)
    {
        this.StringList = songs;
    }


public View getView(final int position, View convertView, ViewGroup parent) {
     Log.i(tag, "Inside get view function");
    // First let's verify the convertView is not null
    if (convertView == null) {
        // This a new view we inflate the new layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_content, parent, false);
    }
        // Now we can fill the layout with the right values
    TextView name = null;
    Songs p = null;
    try{
        name = (TextView) convertView.findViewById(R.id.listViewItem);
        p = StringList.get(position);
        p = db.getSong(p.getID());
    }
    catch(Exception e)
    {
    	Log.i(tag,e.getMessage());
    }
        Log.i(tag, "Song id " + Integer.toString(p.getID()) + "upvotes " + p.getUpvotes());
        final int id = p.getID();
        name.setText(p.getName());
        final TextView upvotes = (TextView) convertView.findViewById(R.id.upvoteCount);
        final TextView downvotes = (TextView) convertView.findViewById(R.id.downvoteCount);
        upvotes.setText(Integer.toString(p.getUpvotes()));
        downvotes.setText(Integer.toString(p.getDownvotes()));
        Log.i(tag,Integer.toString(position) + "    " + Integer.toString(pos));
        final Button upvote = (Button)convertView.findViewById(R.id.upvote);
        final Button downvote = (Button)convertView.findViewById(R.id.downvote);
        final TextView vote = (TextView)convertView.findViewById(R.id.votedByUser);
        if(pos == position)
        {
            boolean found = false;
            for(int i=0;i<songsVotes.size();i++)
            {
                Log.i(tag,"songsVotes contains " + Integer.toString(songsVotes.get(i)) + " and id is " + Integer.toString(id));
                if(songsVotes.get(i) == id)
                {
                    found = true;
                    break;
                }
            }
            upvote.setVisibility(View.VISIBLE);
            downvote.setVisibility(View.VISIBLE);
            upvote.setEnabled(!found);
            downvote.setEnabled(!found);
        	upvote.setVisibility(View.VISIBLE);
        	downvote.setVisibility(View.VISIBLE);
        	upvotes.setVisibility(View.VISIBLE);
        	downvotes.setVisibility(View.VISIBLE);
        	if(vote.getText().length() == 0)
        	{
            	vote.setVisibility(View.GONE);
        	}
        	else
        	{
            	vote.setVisibility(View.VISIBLE);
        	}
        }
        else
        {
        	upvote.setVisibility(View.GONE);
        	downvote.setVisibility(View.GONE);
        	upvotes.setVisibility(View.GONE);
        	downvotes.setVisibility(View.GONE);
        	vote.setVisibility(View.GONE);
        }
        upvote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vote.setText("Upvoted");
				Log.i(tag, "inside on click");
                upvote.setEnabled(false);
                downvote.setEnabled(false);
                Songs song = db.getSong(id);
				Log.i(tag, "Got song with id " + Integer.toString(id) + " and name " + song.getName() + " upvotes " + song.getUpvotes());
				int upvotesCount = song.getUpvotes();
				song.setUpvotes(upvotesCount + 1);
                songsVotes.add(new Integer(id));
				Log.i(tag, "upvotes incremented");
				db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());
				vote.setVisibility(View.VISIBLE);
				Log.i(tag, "database updated " + " upvotes " + db.getSong(id).getUpvotes());
				upvotes.setText(Integer.toString(song.getUpvotes()));
				Log.i(tag, "upvotes shown");
				vote.setTextColor(context.getResources().getColor(R.color.GREEN));
			}
		});
        
        downvote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vote.setText("Downvoted");
				Songs song = db.getSong(id);
                upvote.setEnabled(false);
                downvote.setEnabled(false);
                songsVotes.add(new Integer(id));
                int downvotesCount = song.getDownvotes();
				song.setDownvotes(downvotesCount + 1);
				db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());
				vote.setVisibility(View.VISIBLE);
				downvotes.setText(Integer.toString(song.getDownvotes()));
				vote.setTextColor(context.getResources().getColor(R.color.RED));
			}
		});

        /*for(int i=0;i<10;i++)
		{
			StringList.add("Song"+Integer.toString(i));
		}*/
     
     
    return convertView;
}

public void setPosition(int position)
{
	pos = position;
}
}