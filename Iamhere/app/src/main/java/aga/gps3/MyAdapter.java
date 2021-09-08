package aga.gps3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Context context;
    ArrayList<ContactList> arrayList;

    public MyAdapter(Context context, ArrayList<ContactList> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             view = inflater.inflate(R.layout.activity_rowcontactlist, null);
            TextView t1_name= (TextView) view.findViewById(R.id.contactname);
            TextView t2_number= (TextView) view.findViewById(R.id.contactnumber);
            Button checkBox = (Button) view.findViewById(R.id.checkBox);

            ContactList contactList = arrayList.get(i);
            if(contactList.getNewCheckBox()==1){
                checkBox.setBackgroundResource(R.drawable.checkedbox);
            }else if(contactList.getNewCheckBox()==0){
                checkBox.setBackgroundResource(R.drawable.uncheckedbox);

            }

            t1_name.setText(contactList.getNewContact());
            t2_number.setText(contactList.getNewNumber());


        return view;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }
}
