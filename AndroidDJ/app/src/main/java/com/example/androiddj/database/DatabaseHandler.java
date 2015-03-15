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
<<<<<<< HEAD
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
=======
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

    private static final String TABLE_USERS = "users";

    private static final String KEY_UID = "uid";
    private static final String KEY_COUNT = "count";

    private static final String TABLE_CHOICES = "choices";

    private static final String KEY_UUID = "uuid";
    private static final String KEY_SID = "sid";
    private static final String KEY_FLAG = "flag";


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
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_UID + " TEXT PRIMARY KEY," + KEY_COUNT + " INTEGER" + ")";
        Log.i("Debugging", CREATE_USERS_TABLE);
        try
        {
            db.execSQL(CREATE_USERS_TABLE);
        }
        catch(Exception e)
        {
            Log.i("Debugging","Error in creating the database");
        }

        String CREATE_CHOICES_TABLE = "CREATE TABLE " + TABLE_CHOICES + "("
                + KEY_UUID + " TEXT PRIMARY KEY," + KEY_SID + " INTEGER PRIMARY KEY," + KEY_FLAG + " INTEGER PRIMARY KEY," +
                "PRIMARY KEY (" + KEY_UUID + "," + KEY_SID + ")," + " FOREIGN KEY ("+KEY_UUID+") REFERENCES "+TABLE_USERS+" ("+KEY_UID+")," +
                " FOREIGN KEY ("+KEY_SID+") REFERENCES "+TABLE_SONGS+" ("+KEY_ID+")" + ")";

        Log.i("Debugging", CREATE_USERS_TABLE);
        try
        {
            db.execSQL(CREATE_CHOICES_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHOICES);

        onCreate(db);
    }
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51


    public void addAllSongs(ArrayList<String> songs)
    {
        int i=0;
        for (String s: songs)
        {
            addSong(new Songs(i+1, s));
            i++;
        }
    }

<<<<<<< HEAD
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
=======
    public void addAllUsers(ArrayList<String> users)
    {
        int i=0;
        for (String s: users)
        {
            addUser(new Users (s));
            i++;
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


    public void addUser(Users user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, user.getUID());
        values.put(KEY_COUNT, user.getCount());

        String query = "INSERT INTO " + TABLE_USERS + " VALUES(" + user.getUID() + ",'" +
                Integer.toString(user.getCount())  + ")";
        Log.i("Debugging", query);
        try
        {
            int val = (int) db.insertOrThrow(TABLE_USERS, null, values);
        }
        catch(Exception e)
        {
            Log.i("Debugging",e.getMessage());
        }
>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
		/*try
		{
			db.execSQL(query);
		}
		catch(Exception e)
		{
			Log.i("Debugging", "Exceptoin " + e.getMessage());
		}*/
<<<<<<< HEAD
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
=======
        int val = 1;
        Log.i("Debugging","Insertion value is " + Integer.toString(val));
        db.close();
    }


    public void addChoice(Choices choice)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UUID, choice.getUUID());
        values.put(KEY_SID, choice.getSID());

        String query = "INSERT INTO " + TABLE_CHOICES + " VALUES(" + choice.getUUID() + ",'" +
                Integer.toString(choice.getSID())+ ",'" + Integer.toString(choice.getFlag())  + ")";
        Log.i("Debugging", query);
        try
        {
            int val = (int) db.insertOrThrow(TABLE_CHOICES, null, values);
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


    public Users getUser(String uid)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,new String[]{KEY_UID,KEY_COUNT},
                KEY_UID + "=?",new String[] {uid},null,null,null,null);

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        Users user = new Users(cursor.getString(0),Integer.parseInt(cursor.getString(1)));

        return user;

    }


    public Choices getChoice(String uuid,int sid)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHOICES,new String[]{KEY_UUID,KEY_SID,KEY_FLAG},
                KEY_UUID + "=?"+" AND " + KEY_SID + "=?",new String[] {uuid,Integer.toString(sid)},null,null,null,null);

        if(cursor != null)
        {
            cursor.moveToFirst();
        }

        Choices choice = new Choices(cursor.getString(0),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)));

        return choice;

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


    public ArrayList<Users> getAllUsers()
    {
        ArrayList<Users> users = new ArrayList<Users>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS,null);

        if(cursor.moveToFirst())
        {
            do
            {
                Users user = new Users();
                Log.i("Debugging","adding user " + cursor.getString(1));
                user.setUID(cursor.getString(0));
                user.setCount(Integer.parseInt(cursor.getString(1)));
                users.add(user);
            }while(cursor.moveToNext());
        }

        return users;
    }

    public ArrayList<Choices> getAllChoices()
    {
        ArrayList<Choices> choices = new ArrayList<Choices>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHOICES,null);

        if(cursor.moveToFirst())
        {
            do
            {
                Choices choice = new Choices();
                Log.i("Debugging","adding user " + cursor.getString(1));
                choice.setUUID(cursor.getString(0));
                choice.setSID(Integer.parseInt(cursor.getString(1)));
                choice.setFlag(Integer.parseInt(cursor.getString(2)));

                choices.add(choice);
            }while(cursor.moveToNext());
        }

        return choices;
    }



>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51

    public void deleteSongByName(String name)
    {
        int id;
        SQLiteDatabase db = this.getWritableDatabase();

        int temp = db.delete(TABLE_SONGS, KEY_NAME + "=?", new String[] {name});

        Log.i(tag, "delete value for song " + name + " is " + Integer.toString(temp));
        db.close();
    }
<<<<<<< HEAD
	
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
=======

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




    //Updating a user
    public int updateUser(String uid,int count)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, count);

        return db.update(TABLE_USERS, values, KEY_UID + "=?",new String[] {uid});
    }

    public void deleteSong(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int temp = db.delete(TABLE_SONGS, KEY_ID + "=?", new String[] {Integer.toString(id)});

        Log.i(tag, "delete value for song " + Integer.toString(id) + " is " + Integer.toString(temp));
        db.close();
    }

    public void deleteUser(String uid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int temp = db.delete(TABLE_USERS, KEY_UID + "=?", new String[] {uid});

        Log.i(tag, "delete value for user " + uid + " is " + Integer.toString(temp));
        db.close();
    }

    public void deleteChoice(String uuid, int sid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        int temp = db.delete(TABLE_CHOICES, KEY_UUID + "=?" + " AND " + KEY_SID + "=?", new String[]{uuid, Integer.toString(sid)});

        Log.i(tag, "delete value for choice " + uuid + "," + Integer.toString(sid) + " is " + Integer.toString(temp));
        db.close();
    }

    public void deleteChoicebySong(int sid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_SONGS +" WHERE "+ KEY_SID + "=" + sid );

        Log.i(tag, "All choices for given song deleted.");
        db.close();
    }

    public void deleteChoicebyUser(String uuid)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_SONGS +" WHERE "+ KEY_UUID + "=" + uuid );

        Log.i(tag, "All choices for given user deleted.");
        db.close();
    }

    public void deleteAllChoices()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_CHOICES);

        Log.i(tag, "All songs deleted(Database cleared).");
        db.close();
    }

>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51

    public void deleteAllSongs()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_SONGS);

        Log.i(tag, "All songs deleted(Database cleared).");
        db.close();
    }
<<<<<<< HEAD
	public int getSongsCount()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS,	null);
		return cursor.getCount();
	}
	
=======

    public void deleteAllUsers()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_USERS);

        Log.i(tag, "All users deleted(Database cleared).");
        db.close();
    }




    public int getSongsCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SONGS,	null);
        return cursor.getCount();
    }

    public int getUsersCount()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS,	null);
        return cursor.getCount();
    }

>>>>>>> d659045139ab3ef91b3c77c0ffaeb89c9db7bc51
}
