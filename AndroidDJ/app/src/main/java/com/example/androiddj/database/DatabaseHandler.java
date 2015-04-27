package com.example.androiddj.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class  DatabaseHandler extends SQLiteOpenHelper
{
	private SQLiteDatabase db;
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "SongDatabase.db";
	
	private static final String TABLE_SONGS = "songs";
	
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_STATUS = "status";
	private static final String KEY_UPVOTES = "upvotes";
	private static final String KEY_DOWNVOTES = "downvotes";
	private static final String KEY_AGING = "aging";
    private static final String KEY_FLAG_YOUTUBE = "flag_Youtube";
    private static final String KEY_YOUTUBE_LINK = "link_Youtube";

    private final String tag = "DJ Debugging";
	
	public DatabaseHandler(Context context)
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
		Log.i("Debugging","calling database handler constructor");
		db = getWritableDatabase();
		Log.i("Debugging","called database handler constructor");
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.i("Debugging","database on create called");
		String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_STATUS + " INTEGER," +  KEY_UPVOTES + 
				" INTEGER," + KEY_DOWNVOTES + " INTEGER," + KEY_AGING + " INTEGER," + KEY_FLAG_YOUTUBE + " INTEGER," +
                KEY_YOUTUBE_LINK+" TEXT"+")";
		Log.i("Debugging", CREATE_SONGS_TABLE);
		try
		{
			db.execSQL(CREATE_SONGS_TABLE);
		}
		catch(Exception e)
		{
			Log.i("Debugging","Error in creating the table");
		}
//        db.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
		
		onCreate(db);
	}


    public void addAllSongs(ArrayList<String> songs)
    {
        int i=0;
        for (String s: songs)
        {
            addSong(new Songs(++i, s));
        }
    }

	//adding new song
	public void addSong(Songs song)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_ID, song.getID());
		values.put(KEY_NAME, song.getName());
		values.put(KEY_STATUS, 0);
		values.put(KEY_UPVOTES, 0);
		values.put(KEY_DOWNVOTES, 0);
		values.put(KEY_AGING,0);
        values.put(KEY_FLAG_YOUTUBE,0);
		
		String query = "INSERT INTO " + TABLE_SONGS + " VALUES(" + Integer.toString(song.getID()) + ",'" + 
		song.getName() + "'," + Integer.toString(song.getStatus()) + "," + Integer.toString(song.getUpvotes()) + "," + 
		Integer.toString(song.getDownvotes()) + "," + Integer.toString(song.getAging()) +  "," + Integer.toString(song.getFlag_Youtube()) +")";
		Log.i("Debugging", query);
		try
		{
			int val = (int) db.insertOrThrow(TABLE_SONGS, null, values);
		}
		catch(Exception e)
		{
			Log.i("Debugging",e.getMessage());
		}
		/*try
		{
			db.execSQL(query);
		}
		catch(Exception e)
		{
			Log.i("Debugging", "Exceptoin " + e.getMessage());
		}*/
		int val = 1;
		Log.i("Debugging","Insertion value is " + Integer.toString(val));
		db.close();
	}

    public void addYoutube_Song(Songs song)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, song.getID());
        values.put(KEY_NAME, song.getName());
        values.put(KEY_STATUS, 0);
        values.put(KEY_UPVOTES, 0);
        values.put(KEY_DOWNVOTES, 0);
        values.put(KEY_AGING,0);
        values.put(KEY_FLAG_YOUTUBE,1);
        values.put(KEY_YOUTUBE_LINK,song.get_url());

        String query = "INSERT INTO " + TABLE_SONGS + " VALUES(" + Integer.toString(song.getID()) + ",'" +
                song.getName() + "'," + Integer.toString(song.getStatus()) + "," + Integer.toString(song.getUpvotes()) + "," +
                Integer.toString(song.getDownvotes()) + "," + Integer.toString(song.getAging()) +  "," + Integer.toString(song.getFlag_Youtube()) +")";
        Log.i("Debugging", query);
        Log.i("Youtube1","adding youtube song - "+values.toString());
        try
        {
            int val = (int) db.insertOrThrow(TABLE_SONGS, null, values);
            Log.i("Youtube1","adding youtube- "+val);
        }
        catch(Exception e)
        {
            Log.i("Youtube1",e.getMessage());
        }
		/*try
		{
			db.execSQL(query);
		}
		catch(Exception e)
		{
			Log.i("Debugging", "Exceptoin " + e.getMessage());
		}*/
        int val = 1;
        Log.i("Debugging","Insertion value is " + Integer.toString(val));
        db.close();
    }



    public Songs getSong(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_SONGS,new String[]{KEY_ID,KEY_NAME,KEY_STATUS,KEY_UPVOTES,KEY_DOWNVOTES,KEY_AGING,KEY_FLAG_YOUTUBE,KEY_YOUTUBE_LINK},
				KEY_ID + "=?",new String[] {Integer.toString(id)},null,null,null,null);
		
		if(cursor != null)
		{
			cursor.moveToFirst();
		}
		
		Songs song = new Songs(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),
				Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)),Integer.parseInt(cursor.getString(6)),
                cursor.getString(7));

        db.close();

        cursor.close();

		return song;
		
	}
	
	//getting all songs
	public ArrayList<Songs> getAllSongs()
	{
		ArrayList<Songs> songs = new ArrayList<Songs>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS,null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Songs song = new Songs();
				Log.i(tag,"adding song " + cursor.getString(1));
				song.setID(Integer.parseInt(cursor.getString(0)));
				song.setName(cursor.getString(1));
				song.setStatus(Integer.parseInt(cursor.getString(2)));
				song.setUpvotes(Integer.parseInt(cursor.getString(3)));
				song.setDownvotes(Integer.parseInt(cursor.getString(4)));
				song.setAging(Integer.parseInt(cursor.getString(5)));
                song.setFlag_Youtube(Integer.parseInt(cursor.getString(6)));
                song.set_url(cursor.getString(7));
				songs.add(song);
			}while(cursor.moveToNext());
		}

        db.close();

        cursor.close();

		return songs;
	}

    public ArrayList<Songs> getAllSongsSorted()
    {
        ArrayList<Songs> songs = new ArrayList<Songs>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS + " ORDER BY " + KEY_UPVOTES + "-" + KEY_DOWNVOTES + " DESC",null);

        if(cursor.moveToFirst())
        {
            do {
                Songs song = new Songs();
                Log.i(tag,"adding song in sorted list " + cursor.getString(1));
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setName(cursor.getString(1));
                song.setStatus(Integer.parseInt(cursor.getString(2)));
                song.setUpvotes(Integer.parseInt(cursor.getString(3)));
                song.setDownvotes(Integer.parseInt(cursor.getString(4)));
                song.setAging(Integer.parseInt(cursor.getString(5)));
                song.setFlag_Youtube(Integer.parseInt(cursor.getString(6)));
                song.set_url(cursor.getString(7));
                songs.add(song);
            }while(cursor.moveToNext());
        }

        db.close();

        cursor.close();

        return songs;

    }

    public ArrayList<Songs> getAllSongsSorted(int id)
    {
        ArrayList<Songs> songs = new ArrayList<Songs>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS + " WHERE " + KEY_ID + "<>" + Integer.toString(id) + " ORDER BY " + KEY_UPVOTES + "-" + KEY_DOWNVOTES + " DESC",null);

        if(cursor.moveToFirst())
        {
            do {
                Songs song = new Songs();
                Log.i(tag,"adding song in sorted list " + cursor.getString(1));
                song.setID(Integer.parseInt(cursor.getString(0)));
                song.setName(cursor.getString(1));
                song.setStatus(Integer.parseInt(cursor.getString(2)));
                song.setUpvotes(Integer.parseInt(cursor.getString(3)));
                song.setDownvotes(Integer.parseInt(cursor.getString(4)));
                song.setAging(Integer.parseInt(cursor.getString(5)));
                song.setFlag_Youtube(Integer.parseInt(cursor.getString(6)));
                song.set_url(cursor.getString(7));
                songs.add(song);
            }while(cursor.moveToNext());
        }

        db.close();

        cursor.close();

        return songs;

    }

    public void deleteSongByName(String name)
    {
        int id;
        SQLiteDatabase db = this.getWritableDatabase();

        int temp = db.delete(TABLE_SONGS, KEY_NAME + "=?", new String[] {name});

        Log.i(tag, "delete value for song " + name + " is " + Integer.toString(temp));
        db.close();
    }
	
	//Updating a song
	public int updateSong(int id,int status,int upvotes,int downvotes,int aging)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_UPVOTES, upvotes);
		values.put(KEY_DOWNVOTES, downvotes);
		values.put(KEY_AGING, aging);
		values.put(KEY_STATUS,status);

		int updateVal = db.update(TABLE_SONGS, values, KEY_ID + "=?",new String[] {Integer.toString(id)});

        db.close();

        return updateVal;
	}


    public int updateSongUp(int id, int upvotes)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UPVOTES, upvotes);

        int updateVal = db.update(TABLE_SONGS, values, KEY_ID + "=?",new String[] {Integer.toString(id)});

        db.close();

        return updateVal;
    }


    public int updateSongDown(int id, int downvotes)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DOWNVOTES, downvotes);

        int updateVal = db.update(TABLE_SONGS, values, KEY_ID + "=?",new String[] {Integer.toString(id)});

        db.close();

        return updateVal;
    }
	
	public void deleteSong(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		int temp = db.delete(TABLE_SONGS, KEY_ID + "=?", new String[] {Integer.toString(id)});
		
		Log.i(tag, "delete value for song " + Integer.toString(id) + " is " + Integer.toString(temp));
		db.close();
	}

    public void deleteAllSongs()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_SONGS);

        Log.i(tag, "All songs deleted(Database cleared).");
        db.close();
    }

	public int getSongsCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS,	null);
		int count = cursor.getCount();

        cursor.close();

        db.close();

        return count;
	}
	
}
