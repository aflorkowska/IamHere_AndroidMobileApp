package aga.gps3;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FetchAdressIntentService extends IntentService {

    private ResultReceiver resultReceiver;
    public FetchAdressIntentService(){
        super("FetchAdressIntentService");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            String errorMessage = "";
            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if(location == null)
            {
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1  );
            }
            catch (Exception exception ){
                errorMessage = exception.getMessage();
            }
            if(addresses==null || addresses.isEmpty()){
                delieverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            }else{
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();
                for(int i=0; i<= address.getMaxAddressLineIndex(); i++)
                {
                    addressFragments.add(address.getAddressLine(i));
                }
                delieverResultToReceiver(
                        Constants.SUCCESS_RESULT,
                        TextUtils.join(
                                Objects.requireNonNull(System.getProperty("line.separator")),
                                addressFragments
                        )
                );
            }

        }
    }

    private void delieverResultToReceiver (int resultCode, String addressMessage ){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, addressMessage);
        resultReceiver.send(resultCode, bundle);


    }
}

