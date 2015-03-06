package com.example.androiddj.database;

public class Songs 
{
	private int _id;
	private String _name;
	private int _status;
	private int _upvotes;
	private int _downvotes;
	private int _aging;
	
	public Songs()
	{
		
	}
	
	public Songs(int id,String name,int status)
	{
		this._id = id;
		this._name = name;
		this._status = status;
		_upvotes = 0;
		_downvotes = 0;
		_aging = 0;
	}
	
	public Songs(int id,String name)
	{
		this._id = id;
		this._name = name;
		this._status = 0;
		_upvotes = 0;
		_downvotes = 0;
		_aging = 0;
	}

	
	public Songs(String name,int status)
	{
		this._name = name;
		this._status = status;
		_upvotes = 0;
		_downvotes = 0;
		_aging = 0;
	}
	
	public Songs(int id,String name,int status,int upvotes,int downvotes,int aging)
	{
		this._id = id;
		this._name = name;
		this._status = status;
		this._upvotes = upvotes;
		this._downvotes = downvotes;
		this._aging = aging;
	}
	
	//getting the id of the song
	public int getID()
	{
		return _id;
	}
	
	//id setter method
	public void setID(int id)
	{
		this._id = id;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		this._name = name;
	}
	
	public int getStatus()
	{
		return _status;
	}
	
	public void setStatus(int status)
	{
		this._status = status;
	}
	
	public int getUpvotes()
	{
		return _upvotes;
	}
	
	public void setUpvotes(int upvotes)
	{
		this._upvotes = upvotes;
	}
	
	public int getDownvotes()
	{
		return _downvotes;
	}
	
	public void setDownvotes(int downvotes)
	{
		this._downvotes = downvotes;
	}
	
	public int getAging()
	{
		return _aging;
	}
	
	public void setAging(int aging)
	{
		this._aging = aging;
	}
}
