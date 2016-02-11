package com.mobile.bolt.mobilegrading;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Reader;
import java.util.ArrayList;
import java.util.Arrays;import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public  class PlaceholderFragment extends Fragment {

        ArrayAdapter<String> mForecastAdapter;
        static final int REQUEST_IMAGE_CAPTURE = 1;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Create some dummy data for the ListView.  Here's a sample weekly forecast
//            String[] data = {
//                    "Mon 6/23 - Sunny - 31/17",
//                    "Tue 6/24 - Foggy - 21/8",
//                    "Wed 6/25 - Cloudy - 22/17",
//                    "Thurs 6/26 - Rainy - 18/11",
//                    "Fri 6/27 - Foggy - 21/10",
//                    "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//                    "Sun 6/29 - Sunny - 20/7"
//            };
//            List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));
////            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
           // Now that we have some dummy forecast data, create an ArrayAdapter.
            // The ArrayAdapter will take data from a source (like our dummy forecast) and
            // use it to populate the ListView it's attached to.
//            mForecastAdapter =
//                    new ArrayAdapter<String>(
//                            getActivity(), // The current context (this activity)
//                            R.layout.list_item_forecast, // The name of the layout ID.
//                            R.id.list_item_forecast_textview, // The ID of the textview to populate.
//                            weekForecast);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Button camereaButton =(Button)rootView.findViewById(R.id.beginCamera);
            camereaButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                }
            });

            // Get a reference to the ListView, and attach this adapter to it.
//            ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
//            listView.setAdapter(mForecastAdapter);
//
            return rootView;
        }
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            super.onActivityResult(requestCode, resultCode, data);

            Bitmap bMap = (Bitmap) data.getExtras().get("data");
            Toast.makeText(getContext(),"bit map generated",Toast.LENGTH_LONG).show();
            int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
//copy pixel data from the Bitmap into the 'intArray' array
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
            String contents = null;
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new MultiFormatReader();
            Result result = null;
            try {
                result = reader.decode(bitmap);
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
            if(result==null){
                Log.e("Fatal Error","in Fragmentmain : result returned null");
            }
            contents = result.getText();
            Toast.makeText(getContext(),contents,Toast.LENGTH_LONG).show();


        }

    }
}