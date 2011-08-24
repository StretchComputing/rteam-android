package com.rteam.android.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimpleSettings extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 6;
	private static final String DATABASE_TABLE_NAME = "SimpleSettings";
	private static final String SETTING_NAME = "Name";
	private static final String SETTING_VALUE = "Value";
	
	private static final String DATABASE_TABLE_DELETE =
									"DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME + ";";
						
	
	private static final String DATABASE_TABLE_CREATE =
							"CREATE TABLE " + DATABASE_TABLE_NAME + " (" +
							SETTING_NAME + " TEXT PRIMARY KEY UNIQUE, " + SETTING_VALUE + " TEXT);";
	
	private static SimpleSettings _settings = null;
	public static void initialize(Context context) {
		if (_settings == null) {
			_settings = new SimpleSettings(context);
		}
	}
	public static SimpleSettings get() { return _settings; } 
	
	public SimpleSettings(Context context) {
		super(context, RTeamConstants.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DATABASE_TABLE_DELETE);
		db.execSQL(DATABASE_TABLE_CREATE);
	}
	
	private Cursor queryFor(String name) {
		return getReadableDatabase().query(DATABASE_TABLE_NAME, 
			  	new String[] { SETTING_VALUE }, 
				SETTING_NAME + "=?", 
				new String[] { name }, 
				"", "", "");
	}
	
	public boolean hasValue(String name) {
		Cursor result = queryFor(name);
		boolean hasValue = result.moveToFirst();
		result.close();
		return hasValue;
	}

	public String get(String name) { return get(name, null); }
	public String get(String name, String defaultValue) {
		Cursor result = queryFor(name);
		if (!result.moveToFirst()) {
			result.close();
			return defaultValue;
		} 
		String value = result.getString(0);
		result.close();
		return value;
	}
	
	public Boolean getBoolean(String name) { return getBoolean(name, false); }
	public Boolean getBoolean(String name, Boolean defaultValue) {
		String val = get(name);
		return val != null ? new Boolean(val) : defaultValue;
	}
 	
	public Integer getInteger(String name) { return getInteger(name, null); }
	public Integer getInteger(String name, Integer defaultValue) {
		String val = get(name);
		return val != null ? new Integer(val) : defaultValue;
	}
	
	public Double getDouble(String name) { return getDouble(name, null); }
	public Double getDouble(String name, Double defaultValue) {
		String val = get(name);
		return val != null ? new Double(val) : defaultValue;
	}
	
	public int count(String name) {
		Cursor result = queryFor(name);
		int count = result.getCount();
		result.close();
		return count;
	}
	public boolean exists(String name) {
		return count(name) > 0;
	}
	
		
	public void set(String name, String value) {
		ContentValues values = new ContentValues();
		values.put(SETTING_NAME, name);
		values.put(SETTING_VALUE, value);
				
		if (exists(name)) {
			getWritableDatabase().update(DATABASE_TABLE_NAME, values, SETTING_NAME + "=?", new String[] { name });
		}
		else {
			getWritableDatabase().insert(DATABASE_TABLE_NAME, SETTING_VALUE, values);
		}
	}
	public void set(String name, Boolean value) { set(name, value.toString()); }
	public void set(String name, Integer value) { set(name, value.toString()); }
	public void set(String name, Double value)  { set(name, value.toString()); }
}
