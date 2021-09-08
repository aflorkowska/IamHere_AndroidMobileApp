package aga.gps3;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.provider.FontsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    DbHelper dbHelper;
    ArrayList<ContactList> arrayList;
    ListView lstTask;
    Button add_btn;
    MyAdapter myAdapter;
    boolean ischecked=true;
    Animation rotate;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        dbHelper = new DbHelper(this);
        add_btn = findViewById(R.id.action_add_task);
        lstTask = (ListView) findViewById(R.id.lstTask);
        arrayList= new ArrayList<>();
        loadTaskList();

    }

    private void loadTaskList() {
       arrayList = dbHelper.getTaskList();
        myAdapter = new MyAdapter(this, arrayList);
        lstTask.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.insertnewcontact, null);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add New Contact")
                        .setMessage("Enter the data below")
                        .setView(view)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editname1 = view.findViewById(R.id.edit_name);
                                EditText editnumber1 = view.findViewById(R.id.edit_number);
                                String number = String.valueOf(editname1.getText());
                                String contact = String.valueOf(editnumber1.getText());
                                dbHelper.insertNewTask(number, contact);
                                loadTaskList();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.contactname);
        String task = String.valueOf(taskTextView.getText());
        dbHelper.deleteTask(task);
        loadTaskList();
    }

    public void updateTask(View view) {
        View parent = (View) view.getParent();
        TextView oldnumber1 = (TextView) parent.findViewById(R.id.contactnumber);
        TextView oldname1 = (TextView) parent.findViewById(R.id.contactname);
        final String oldnumber = String.valueOf(oldnumber1.getText());
        final String oldname = String.valueOf(oldname1.getText());
        showInputBox(oldname, oldnumber);

    }

    public void checkBox(View view){
        View parent = (View) view.getParent();
        Button checkBox = (Button) parent.findViewById(R.id.checkBox);
        checkBox.startAnimation(rotate);
        TextView oldnumber1 = (TextView) parent.findViewById(R.id.contactnumber);
        final String oldnumber = String.valueOf(oldnumber1.getText());
        if(checkBox.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.checkedbox).getConstantState()))
        {
            ischecked=true;
        }else if(checkBox.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.uncheckedbox).getConstantState()))
        {
            ischecked=false;
        }
        ischecked=!ischecked;

        if(ischecked==true)
        {
            checkBox.setBackgroundResource(R.drawable.checkedbox);
            dbHelper.checkBoxState(1, oldnumber);

        }
        else if(ischecked==false)
        {
            checkBox.setBackgroundResource(R.drawable.uncheckedbox);
            dbHelper.checkBoxState(0, oldnumber);

        }
    }


    public void showInputBox(final String oldname, final String oldnumber){

        LayoutInflater inflater = getLayoutInflater();
        final View view2 = inflater.inflate(R.layout.insertnewcontact, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Add New Contact");
                dialog.setMessage("Enter the data below");
                dialog.setView(view2);
                 final EditText editname1 = view2.findViewById(R.id.edit_name);
                 final EditText editnumber1 = view2.findViewById(R.id.edit_number);
                editname1.setText(oldname);
                editnumber1.setText(oldnumber);
                dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String contact = String.valueOf(editname1.getText());
                        String  number = String.valueOf(editnumber1.getText());
                       dbHelper.updateTask(number,contact,oldnumber);
                        loadTaskList();

                    }
                });
                dialog.setNegativeButton("Cancel", null);
                dialog.create();
        dialog.show();

    }


}