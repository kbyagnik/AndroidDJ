package com.example.androiddj.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper
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
				" INTEGER," + KEY_DOWNVOTES + " INTEGER," + KEY_AGING + " INTEGER" + ")";
		Log.i("Debugging", CREATE_SONGS_TABLE);
		try
		{
			db.execSQL(CREATE_SONGS_TABLE);
		}
		catch(Exception e)
		{
			Log.i("Debugging","Error in creating the database");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
		
		onCreate(db);
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
		
		String query = "INSERT INTO " + TABLE_SONGS + " VALUES(" + Integer.toString(song.getID()) + ",'" + 
		song.getName() + "'," + Integer.toString(song.getStatus()) + "," + Integer.toString(song.getUpvotes()) + "," + 
		Integer.toString(song.getDownvotes()) + "," + Integer.toString(song.getAging()) + ")";
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
	
	public Songs getSong(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_SONGS,new String[]{KEY_ID,KEY_NAME,KEY_STATUS,KEY_UPVOTES,KEY_DOWNVOTES,KEY_AGING},
				KEY_ID + "=?",new String[] {Integer.toString(id)},null,null,null,null);
		
		if(cursor != null)
		{
			cursor.moveToFirst();
		}
		
		Songs song = new Songs(Integer.parseInt(cursor.getString(0)),cursor.getString(1),Integer.parseInt(cursor.getString(2)),
				Integer.parseInt(cursor.getString(3)),Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
		
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
				Log.i("Debugging","adding song " + cursor.getString(1));
				song.setID(Integer.parseInt(cursor.getString(0)));
				song.setName(cursor.getString(1));
				song.setStatus(Integer.parseInt(cursor.getString(2)));
				song.setUpvotes(Integer.parseInt(cursor.getString(3)));
				song.setDownvotes(Integer.parseInt(cursor.getString(4)));
				song.setAging(Integer.parseInt(cursor.getString(5)));
				songs.add(song);
			}while(cursor.moveToNext());
		}
		
		return songs;
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
		
		return db.update(TABLE_SONGS, values, KEY_ID + "=?",new String[] {Integer.toString(id)});
	}
	
	public void deleteSong(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		int temp = db.delete(TABLE_SONGS, KEY_ID + "=?", new String[] {Integer.toString(id)});
		
		Log.i(tag, "delete value for song " + Integer.toString(id) + " is " + Integer.toString(temp));
		db.close();
	}
	
	public int getSongsCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS,	null);
		return cursor.getCount();
	}
	
}
