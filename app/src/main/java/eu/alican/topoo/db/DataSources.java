package eu.alican.topoo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.alican.topoo.models.Todo;
import eu.alican.topoo.models.User;

/**
 * Project: Topoo
 * Created by alican on 14.03.2016.
 */
public class DataSources {

    Context context;
    SQLiteOpenHelper dbHelper;

    SQLiteDatabase db;

    public DataSources(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(context);


    }

    public void insertUser(User user, String password){

        db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            //values.put(DbHelper.KEY_USER_ID, user.getId());
            values.put(DbHelper.KEY_USER_NAME, user.getName());
            values.put(DbHelper.KEY_USER_PASSWORD, password);

            db.insertOrThrow(DbHelper.TABLE_USER, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.wtf("DataSources:addUser", "ERROR while trying to add a new User to database");
        }finally {
            db.endTransaction();
        }

    }


    public void insertTodo(Todo todo){


        db = dbHelper.getWritableDatabase();


        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());


        db.beginTransaction();
        try {

            //values.put(DbHelper.KEY_USER_ID, user.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbHelper.KEY_TODO_ID, todo.getId());
            contentValues.put(DbHelper.KEY_TODO_TEXT, todo.getText());
            contentValues.put(DbHelper.KEY_TODO_CHECKED, todo.isChecked());
            contentValues.put(DbHelper.KEY_TODO_PRIORITY, todo.isPriority());
            contentValues.put(DbHelper.KEY_TODO_NAME, todo.getName());
            contentValues.put(DbHelper.KEY_TODO_USER, todo.getUser());
            contentValues.put(DbHelper.KEY_TODO_DEADLINE, dateFormat.format(todo.getDeadline()));

            db.insertOrThrow(DbHelper.TABLE_TODO, null, contentValues);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.wtf("DataSources:insertTodo", "ERROR while trying to add a new Todo to database");
        }finally {
            db.endTransaction();
        }

    }

    public Todo getToDoById(long id){

        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_TODO +
                " WHERE " + DbHelper.KEY_TODO_ID+ "=" + id;



        //Cursor cursor = db.query(DbHelper.TABLE_TODO, null, DbHelper.KEY_TODO_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);


        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            int index_id = cursor.getColumnIndex(DbHelper.KEY_TODO_ID);
            int index_name = cursor.getColumnIndex(DbHelper.KEY_TODO_NAME);
            int index_text = cursor.getColumnIndex(DbHelper.KEY_TODO_TEXT);
            int index_created = cursor.getColumnIndex(DbHelper.KEY_TODO_CREATED);
            int index_deadline = cursor.getColumnIndex(DbHelper.KEY_TODO_DEADLINE);
            int index_priority = cursor.getColumnIndex(DbHelper.KEY_TODO_PRIORITY);
            int index_checked = cursor.getColumnIndex(DbHelper.KEY_TODO_CHECKED);
            int index_user = cursor.getColumnIndex(DbHelper.KEY_TODO_USER);

            java.util.Date created = null;
            java.util.Date deadline = null;

            cursor.moveToFirst();

            try {
                created = sdf.parse(cursor.getString(index_created));
                if (cursor.getString(index_deadline) != null){
                    deadline = sdf.parse(cursor.getString(index_deadline));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Todo todo = new Todo(
                    cursor.getLong(index_id),
                    cursor.getString(index_name),
                    cursor.getString(index_text),
                    created,
                    deadline,
                    cursor.getLong(index_user),
                    (cursor.getInt(index_priority) == 1),
                    (cursor.getInt(index_checked) == 1)
            );
            cursor.close();
            return todo;
        }
        return null;
    }




    public User getUserByName(String username, String password){

        db = dbHelper.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DbHelper.TABLE_USER +
                " WHERE " + DbHelper.KEY_USER_NAME + "= '" + username +
                "' AND "  + DbHelper.KEY_USER_PASSWORD+ "= '" + password + "'";

        //Cursor cursor = db.query(DbHelper.TABLE_TODO, null, DbHelper.KEY_TODO_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null){

            int index_id = cursor.getColumnIndex(DbHelper.KEY_USER_ID);
            int index_username = cursor.getColumnIndex(DbHelper.KEY_USER_NAME);

            if(cursor.moveToFirst() && cursor.getCount() >= 1){
                User user = new User();
                user.setName(cursor.getString(index_username));
                user.setId(cursor.getLong(index_id));
                cursor.close();
                return user;
            }

        }
        return null;
    }

    public void updateTodo(Todo todo){
        db = dbHelper.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.KEY_TODO_ID, todo.getId());
        contentValues.put(DbHelper.KEY_TODO_TEXT, todo.getText());
        contentValues.put(DbHelper.KEY_TODO_CHECKED, todo.isChecked());
        contentValues.put(DbHelper.KEY_TODO_PRIORITY, todo.isPriority());
        contentValues.put(DbHelper.KEY_TODO_NAME, todo.getName());
        contentValues.put(DbHelper.KEY_TODO_USER, todo.getUser());

        if(todo.getDeadline() != null){
            contentValues.put(DbHelper.KEY_TODO_DEADLINE, dateFormat.format(todo.getDeadline()));
        }
        db.update(DbHelper.TABLE_TODO, contentValues, DbHelper.KEY_TODO_ID + " = " + todo.getId(), null);
        db.close();

    }

    public List<Todo> getTodos(long userId, boolean showChecked, boolean priority){
        db = dbHelper.getReadableDatabase();
        List<Todo> todos = new ArrayList<>();

        String select = DbHelper.KEY_TODO_USER + "=?";

        String orderBy = DbHelper.KEY_TODO_DEADLINE;
        if (priority){
            orderBy = DbHelper.KEY_TODO_PRIORITY + " DESC " + ", " + orderBy;
        }

        if(showChecked){
            select += " AND NOT " + DbHelper.KEY_TODO_CHECKED + "= 1";
        }

        Cursor cursor = db.query(DbHelper.TABLE_TODO,
                null,
                select,
                new String[] { String.valueOf(userId) }, null, null,
                orderBy);
        cursor.moveToFirst();
        if (cursor.getCount() == 0){
            cursor.close();
            return todos;
        }


        int index_id = cursor.getColumnIndex(DbHelper.KEY_TODO_ID);
        int index_name = cursor.getColumnIndex(DbHelper.KEY_TODO_NAME);
        int index_text = cursor.getColumnIndex(DbHelper.KEY_TODO_TEXT);
        int index_created = cursor.getColumnIndex(DbHelper.KEY_TODO_CREATED);
        int index_deadline = cursor.getColumnIndex(DbHelper.KEY_TODO_DEADLINE);
        int index_priority = cursor.getColumnIndex(DbHelper.KEY_TODO_PRIORITY);
        int index_checked = cursor.getColumnIndex(DbHelper.KEY_TODO_CHECKED);
        int index_user = cursor.getColumnIndex(DbHelper.KEY_TODO_USER);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        while (!cursor.isAfterLast()){

            java.util.Date created = null;
            java.util.Date deadline = null;


            try {
                created = sdf.parse(cursor.getString(index_created));
                if (cursor.getString(index_deadline) != null){
                    deadline = sdf.parse(cursor.getString(index_deadline));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }



            Todo todo = new Todo(
                    cursor.getLong(index_id),
                    cursor.getString(index_name),
                    cursor.getString(index_text),
                    created,
                    deadline,
                    cursor.getLong(index_user),
                    (cursor.getInt(index_priority) == 1),
                    (cursor.getInt(index_checked) == 1)
            );            todos.add(todo);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return todos;
    }



    public List<User> getUsers(){
        db = dbHelper.getReadableDatabase();
        List<User> users = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.TABLE_USER, null, null, null, null, null, DbHelper.KEY_USER_ID);
        cursor.moveToFirst();
        if (cursor.getCount() == 0){
            cursor.close();
            return users;
        }
        int index_id = cursor.getColumnIndex(DbHelper.KEY_USER_ID);
        int index_name = cursor.getColumnIndex(DbHelper.KEY_USER_NAME);

        while (!cursor.isAfterLast()){

            User user = new User();
            user.setId(cursor.getLong(index_id));
            user.setName(cursor.getString(index_name));
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return users;
    }


    public void updatePassword(User user, String password){
        db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.KEY_USER_PASSWORD, password);

        db.update(DbHelper.TABLE_USER, contentValues, DbHelper.KEY_USER_ID + " = " + user.getId(), null);
        db.close();

    }

    public void deleteTodoById(long id){
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_TODO, "id = '" + id + "'", null);
        db.close();
    }

    public void deleteAllTodos(long userId){
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_TODO, DbHelper.KEY_TODO_USER + " = '" + userId + "'", null);
        db.close();
    }
    public void deleteUserById(long id){
        db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_USER, "id = '" + id + "'", null);
        db.close();
    }





}
