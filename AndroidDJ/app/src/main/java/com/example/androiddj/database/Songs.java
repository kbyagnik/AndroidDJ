package com.example.androiddj.database;


public class Songs
{
	private int _id;
	private String _url;
    private String _name;
	private int _status;
	private int _upvotes;
	private int _downvotes;
	private int _aging;
    private int _flag_Youtube;
	
	public Songs()
	{
		
	}
	
	public Songs(int id,String name,int status)
	{
		this._id = id;
		this._name = name;
        this._url = "";
		this._status = status;
		_upvotes = 0;
		_downvotes = 0;
		_aging = 0;
        _flag_Youtube = 0;

	}
	
	public Songs(int id,String name)
	{
		this._id = id;
		this._name = name;
        this._url = "";
		this._status = 0;
		_upvotes = 0;
		_downvotes = 0;
		_aging = 0;
        _flag_Youtube=0;
	}

	
	public Songs(String name,int status)
	{
		this._name = name;
        this._url = "";
		this._status = status;
		_upvotes = 0;
		_downvotes = 0;
		_aging = 0;
        _flag_Youtube=0;
	}
	
	public Songs(int id,String name,int status,int upvotes,int downvotes,int aging)
	{
		this._id = id;
		this._name = name;
        this._url = "";
		this._status = status;
		this._upvotes = upvotes;
		this._downvotes = downvotes;
		this._aging = aging;
        this._flag_Youtube=0;
	}

    public Songs(int id,String name,int status,int upvotes,int downvotes,int aging, int flag, String link)
    {
        this._id = id;
        this._name = name;
        this._url = "";
        this._status = status;
        this._upvotes = upvotes;
        this._downvotes = downvotes;
        this._aging = aging;
        this._flag_Youtube=flag;
        this._url = link;
    }


    public Songs(int id, String url, String title, int flag)
    {
        this._id = id;
        this._name = title;
        this._url = url;
        this._status = 0;
        _upvotes = 0;
        _downvotes = 0;
        _aging = 0;
        this._flag_Youtube=flag;
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

    public void setFlag_Youtube(int flag) { this._flag_Youtube=flag; }

    public int getFlag_Youtube() { return _flag_Youtube; }

    public void set_url(String url) { this._url=url; }

    public String get_url() { return _url; }
}
