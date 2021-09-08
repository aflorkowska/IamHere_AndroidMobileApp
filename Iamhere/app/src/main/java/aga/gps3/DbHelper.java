package aga.gps3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="MyContactList";
    private static final int DB_VER = 1;
    public static final String DB_TABLE="ContactList";
    public static final String DB_NUMBER = "ContactNumber";
    public static final String DB_CONTACT = "ContactName";
    public static final String DB_CHECKBOX = "CheckBox";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL , %s TEXT NOT NULL,%s NUMERIC );",DB_TABLE,DB_NUMBER,DB_CONTACT,DB_CHECKBOX );
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s",DB_TABLE);
        db.execSQL(query);
        onCreate(db);

    }

    public void insertNewTask(String number, String contactname){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_NUMBER,number);
        values.put(DB_CONTACT,contactname);
        values.put(DB_CHECKBOX,1);
        db.insertWithOnConflict(DB_TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void deleteTask(String number){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE,DB_NUMBER + " = ?",new String[]{number});
        db.close();
    }

    public ArrayList<ContactList> getTaskList(){
        ArrayList<ContactList> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,new String[]{DB_NUMBER, DB_CONTACT,DB_CHECKBOX},null,null,null,null,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_NUMBER);
            int index2 = cursor.getColumnIndex(DB_CONTACT);
            int index3 = cursor.getColumnIndex(DB_CHECKBOX);
            taskList.add(new ContactList(cursor.getString(index2), cursor.getString(index),cursor.getInt(index3) ));

        }
        cursor.close();
        db.close();
        return taskList;
    }

    public void updateTask(String name, String number, String oldnumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_NUMBER,number);
        values.put(DB_CONTACT,name);
        db.update(DB_TABLE,values,DB_CONTACT + " = ?", new String[]{oldnumber});
        db.close();
    }

    public void checkBoxState(int a, String number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_CHECKBOX,a);
        db.update(DB_TABLE,values,DB_CONTACT + " = ?", new String[]{number});
        db.close();

    }


}
