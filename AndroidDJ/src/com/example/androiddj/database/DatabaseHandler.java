package com.example.androiddj.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "SongDatabase";
	
	private static final String TABLE_SONGS = "songs";
	
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_STATUS = "status";
	private static final String KEY_UPVOTES = "upvotes";
	private static final String KEY_DOWNVOTES = "downvotes";
	private static final String KEY_AGING = "aging";
	
	public DatabaseHandler(Context context)
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
				+ KEY_ID + "INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_STATUS + " INTEGER," +  KEY_UPVOTES + 
				" INTEGER," + KEY_DOWNVOTES + " INTEGER," + KEY_AGING + " INTEGER" + ")";
		db.execSQL(CREATE_SONGS_TABLE);
	}
	
	public void onUpgrade(SQLiteDatabase db)
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
		
		db.insert(TABLE_SONGS, null, values);
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
		
		return db.update(TABLE_SONGS, values, KEY_ID + "=?",new String[] {Integer.toString(id)});
	}
	
	public void deleteSong(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SONGS, KEY_ID + "=?", new String[] {Integer.toString(id)});
		db.close();
	}
	
	public int getSongsCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS,	null);
		return cursor.getCount();
	}
	
}
