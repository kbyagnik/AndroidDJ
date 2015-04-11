package com.example.androiddj;

import java.util.ArrayList;
import java.util.HashSet;
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

public class ListViewAdapterClient extends ArrayAdapter<Songs>
{
	 
private List<Songs> StringList;
private Context context;
private int pos;
final String tag = "DJ Debugging";
final ClientView client;
final DatabaseHandler db;
private HashSet<Integer> songsUpvoted;
private HashSet<Integer> songsDownvoted;


    public ListViewAdapterClient(List<Songs> StringList, Context ctx,int position,DatabaseHandler db) {
    super(ctx, R.layout.listview_content, StringList);
    Log.i(tag, "Inside list view adapter constructor");
    pos = position;
    this.StringList = StringList;
    this.context = ctx;
    this.client = (ClientView) ctx;
    this.db = db;
    songsUpvoted = new HashSet<Integer>();
    songsDownvoted = new HashSet<Integer>();

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
        if(StringList.size() == 0)
        {
        	Log.i(tag, "List is empty");
        }
        else
        {
            final TextView name = (TextView) convertView.findViewById(R.id.listViewItem);
	        final Songs p = StringList.get(position);
	        Log.i(tag, "Song id " + Integer.toString(p.getID()));
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
                boolean upvoted = songsUpvoted.contains(id);
                boolean downvoted = songsDownvoted.contains(id);
                upvote.setVisibility(View.VISIBLE);
                downvote.setVisibility(View.VISIBLE);
                upvote.setEnabled(!(upvoted || downvoted));
                downvote.setEnabled(!(upvoted || downvoted));
                upvotes.setVisibility(View.VISIBLE);
	        	downvotes.setVisibility(View.VISIBLE);
	        	if(upvoted)
                {
                    vote.setText("Upvoted");
                    vote.setVisibility(View.VISIBLE);
                    vote.setTextColor(context.getResources().getColor(R.color.GREEN));
                }
                else if(downvoted)
                {
                    vote.setText("Downvoted");
                    vote.setVisibility(View.VISIBLE);
                    vote.setTextColor(context.getResources().getColor(R.color.RED));
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
					Log.i(tag, "Got song with id " + Integer.toString(id));
					int upvotesCount = p.getUpvotes();
                    upvote.setEnabled(false);
                    songsUpvoted.add(new Integer(id));
                    downvote.setEnabled(false);
					//send json string to host with upvote request for the song
					
					Log.i(tag, "Incrementing upvotes by sending upvote request to host");
					/*song.setUpvotes(upvotesCount + 1);
					Log.i(tag, "upvotes incremented");
					db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());*/
					vote.setVisibility(View.VISIBLE);
					//Log.i(tag, "database updated " + " upvotes " + song.getUpvotes());
					upvotes.setText(Integer.toString(upvotesCount + 1));
					Log.i(tag, "upvotes shown");
					vote.setTextColor(context.getResources().getColor(R.color.GREEN));
				}
			});
	        
	        downvote.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					vote.setText("Downvoted");
					/*Songs song = db.getSong(id);*/
                    upvote.setEnabled(false);
                    downvote.setEnabled(false);
					//send json string to host with downvote request for the song
					int downvotesCount = p.getDownvotes();
                    songsDownvoted.add(new Integer(id));
                    //song.setDownvotes(downvotesCount + 1);
					/*db.updateSong(id, song.getStatus(),song.getUpvotes(),song.getDownvotes(),song.getAging());*/
					vote.setVisibility(View.VISIBLE);
					downvotes.setText(Integer.toString(downvotesCount + 1));
					vote.setTextColor(context.getResources().getColor(R.color.RED));
				}
			});
	
	        /*for(int i=0;i<10;i++)
			{
				StringList.add("Song"+Integer.toString(i));
			}*/
	     
        }
    return convertView;
}

public void setPosition(int position)
{
	pos = position;
}

    public void setList(ArrayList<Songs> songs)
    {
        StringList.clear();
        StringList.addAll(songs);
        Log.i(tag,"a" + Integer.toString(songs.size()) + "b");
        Log.i(tag,"a" + Integer.toString(StringList.size()) + "b");
        try {
            Log.i(tag, songs.get(9).getName());
        }
        catch(Exception e)
        {
            Log.i(tag,e.toString());
        }
        notifyDataSetChanged();
    }

}