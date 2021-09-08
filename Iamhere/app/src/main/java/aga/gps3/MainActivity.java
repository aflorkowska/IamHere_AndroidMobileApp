package aga.gps3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_LOCATION_PERMISION = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private TextView textLatLong, textAddress;
    private ResultReceiver resultReceiver;
    private double latitude;
    private double longitude;
    private boolean emptylist=false;
    private boolean ischeckedContact=false;
    private boolean first_loc_check=true;
    private boolean btn_loc_clicked=false;
    String  msg,coord, finalnie,lat,log;
    Button mapa;
    Button send;
    TextView text;
    Button contactlist;
    Button btn_location;
    DbHelper dbHelper;
    ImageView icon;
    ImageView back;
    Animation btn_send_appear;
    Animation icon_appear;
    Animation icon_disappear;
    Animation txt_loc_appear;
    Animation btn_send_disappear;
    Animation txt_loc_disappear;
    Animation from_bottom;




    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //animations
        btn_send_appear = AnimationUtils.loadAnimation(this, R.anim.btn_send_appear);
        txt_loc_appear = AnimationUtils.loadAnimation(this, R.anim.txt_loc_appear);
        icon_appear = AnimationUtils.loadAnimation(this, R.anim.icon_appear);
        icon_disappear = AnimationUtils.loadAnimation(this, R.anim.icon_disappear);
        btn_send_disappear = AnimationUtils.loadAnimation(this, R.anim.btn_send_disappear);
        txt_loc_disappear = AnimationUtils.loadAnimation(this, R.anim.txt_loc_disappear);
        from_bottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        //options
        icon = findViewById(R.id.imageView);
        text = findViewById(R.id.text);
        back = findViewById(R.id.back);
        dbHelper = new DbHelper(this);
        textLatLong = findViewById(R.id.textLatLong);
        textAddress = findViewById(R.id.textAdress);
        send = findViewById(R.id.btnsend);
        btn_location = findViewById(R.id.btnAktualnalokalizacja);
        mapa = findViewById(R.id.mapa);
        contactlist =  findViewById(R.id.contactlist);
        //app starts
        icon.setAnimation(icon_appear);
        btn_location.setAnimation(from_bottom);
        contactlist.setAnimation(from_bottom);
        mapa.setAnimation(from_bottom);
        //Internet
       if(haveNetwork())
        {}
        //GPS
        if(isGPSEnabled())
        {}
        //Mapa
        if (isServicesOK()) {

                init();

        }

        //ContactsList
        importcontacts();


        //Sms
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTextMessage();
                if(emptylist==false && ischeckedContact==true){
                    first_loc_check=true;
                    icon.startAnimation(icon_appear);
                    send.startAnimation(icon_disappear);
                    back.startAnimation(txt_loc_disappear);
                    textLatLong.startAnimation(txt_loc_disappear);
                    textAddress.startAnimation(txt_loc_disappear);
                    icon.setVisibility(View.VISIBLE);
                    textLatLong.setVisibility( View.INVISIBLE);
                    textAddress.setVisibility( View.INVISIBLE);
                    send.setVisibility( View.INVISIBLE);
                    back.setVisibility( View.INVISIBLE);
                    text.setVisibility(View.INVISIBLE);
                }


            }
        });

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);

            }
        }

        //Adres i koordynaty
        resultReceiver = new AddressResultReceiver(new Handler());

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //animations
                btn_loc_clicked=true;
                if (first_loc_check == true) {
                    first_loc_check=false;
                    send.setVisibility(View.VISIBLE);
                    send.startAnimation(btn_send_appear);
                    back.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    back.startAnimation(txt_loc_appear);
                    icon.setAnimation(icon_disappear);
                    icon.setVisibility(View.INVISIBLE);


                    if (ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISION
                        );
                    } else {
                        getCurrentLocation();
                    }

                } else {

                    if (ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISION
                        );
                    } else {
                        getCurrentLocation();
                    }
                }

            }
        });
    }

    private boolean haveNetwork(){
        boolean have_Internet = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info : networkInfos)
        {
            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if(info.isConnected()){
                    have_Internet=true;
                }
            }
            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()){
                    have_Internet=true;
                }
            }
        }

        if(have_Internet){
            return true;
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Internet Permissions")
                    .setMessage("Internet is required for this app to work properly. Please enable access to Internet!")
                    .setPositiveButton("Yes", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent =  new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivityForResult(intent, 0);
                        }
                    }))
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }

        return false;
    }

    private  boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(providerEnabled){
            return true;
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work properly. Please enable GPS!")
                    .setPositiveButton("Yes", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent =  new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent,REQUEST_CODE_LOCATION_PERMISION);
                        }
                    }))
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }
        return false;


    }
    protected  void sendTextMessage(){
        msg = textAddress.getText().toString();
        coord = textLatLong.getText().toString();
        lat = Double.toString(latitude); //cos sie popsulo i nie dziala
        log = Double.toString(longitude);
        finalnie = "I am here: " + msg ; //+ " My coordinates: " + lat + ", " + log ;


        SmsManager smsManager = SmsManager.getDefault();


        ArrayList<ContactList> taskList = dbHelper.getTaskList();
        if(taskList.size()<=0)
        {

            Toast.makeText(this, "The contact list is empty. Please complete it!", Toast.LENGTH_SHORT).show();
            emptylist=true;
        }
        else {

            for (int i = 0; i < taskList.size(); i++) {
                emptylist=false;
                ContactList contactList = taskList.get(i);
                if(contactList.getNewCheckBox()==1) {
                    ischeckedContact=true;
                    String phoneNo = contactList.getNewNumber();
                    smsManager.sendTextMessage(phoneNo, null, finalnie, null, null);
                    Toast.makeText(this, "Sent!", Toast.LENGTH_SHORT).show();
                }
                if(ischeckedContact==false)
                {
                    Toast.makeText(this, "Sms can not be sent. Please check your contact list!", Toast.LENGTH_SHORT).show();
                }

            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for permitting!", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for permitting!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_LOCATION_PERMISION ) {
            if (isGPSEnabled()) {
                Toast.makeText(this, "GPS is enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS is not enabled! Unable to show user location", Toast.LENGTH_SHORT).show();
            }
        }



    }

    private void getCurrentLocation() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size()>0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        textLatLong.setText(
                                String.format(
                                        "Latitude: %s\nLongitude: %s",
                                        latitude,
                                        longitude
                                )
                        );
                        textLatLong.setVisibility(View.VISIBLE);
                        textLatLong.startAnimation(txt_loc_appear);
                        Location location = new Location("providerNA");
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        fetchAddressFromLatLong(location);
                        } else{

                        }
                    }
                }, Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(this,FetchAdressIntentService.class );
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }

    private  class AddressResultReceiver extends ResultReceiver{
         AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == Constants.SUCCESS_RESULT ){
                textAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                textAddress.setVisibility(View.VISIBLE);
                textAddress.startAnimation(txt_loc_appear);
            }else{
                Toast.makeText(MainActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void init() {
        mapa.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if(btn_loc_clicked==true){
                                            Intent intent = new Intent(MainActivity.this, MapActivity.class);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(getApplicationContext() , "Firstly you have to check where you are. Please click button - My current location.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
        );


    }

    private void importcontacts() {
        contactlist.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                                        startActivity(intent);
                                    }
                                }
        );


    }

    public boolean isServicesOK (){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK:  Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK:  an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }return false;

    }


}
