package eu.alican.topoo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.alican.topoo.models.User;

/**
 * Project: Topoo
 * Created by alican on 14.03.2016.
 */
public class DbHelper extends SQLiteOpenHelper {


    private static DbHelper sInstance;


    private static final String DATABASE_NAME = "topoo";
    private static final int DATABASE_VERSION = 4;



    public static final String TABLE_USER = "users";
    public static final String TABLE_TODO = "todos";


    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_PASSWORD = "password";


    public static final String KEY_TODO_ID = "id";
    public static final String KEY_TODO_NAME = "name";
    public static final String KEY_TODO_TEXT = "text";
    public static final String KEY_TODO_CREATED = "created";
    public static final String KEY_TODO_DEADLINE = "deadline";
    public static final String KEY_TODO_USER = "user_id";
    public static final String KEY_TODO_PRIORITY = "priority";
    public static final String KEY_TODO_CHECKED = "checked";



    private DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_USER_NAME + " TEXT UNIQUE," +
                KEY_USER_PASSWORD + " TEXT" +
                ")";
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY, " +
                KEY_TODO_NAME + " TEXT, " +
                KEY_TODO_TEXT + " TEXT, " +
                KEY_TODO_CREATED + " datetime DEFAULT CURRENT_TIMESTAMP, " +
                KEY_TODO_DEADLINE + " datetime, " +
                KEY_TODO_PRIORITY + " INTEGER," +
                KEY_TODO_CHECKED + " INTEGER," +
                KEY_TODO_USER + " INTEGER," +
                " FOREIGN KEY ("+ KEY_TODO_USER + ") " +
                "REFERENCES " + TABLE_USER + "("+ KEY_USER_ID +") ON DELETE CASCADE);";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(db);
        }
    }

    public static synchronized DbHelper getInstance(Context context){
        if (sInstance == null){
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }






}
