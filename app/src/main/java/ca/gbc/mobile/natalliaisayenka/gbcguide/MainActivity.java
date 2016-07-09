package ca.gbc.mobile.natalliaisayenka.gbcguide;
/*Natallia Isayenka
* SI 100744884
*created: 04-11-2014
* lastEdit: 17-11-2014
* */
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    private final LatLng CasaLomaCampus = new LatLng(43.6758, -79.4107);
    private final LatLng SaintJamesCampus = new LatLng(43.650931, -79.370266);
    private final LatLng RyersonUniversity = new LatLng(43.6577, -79.3802);
    private final LatLng WaterfrontCampus = new LatLng(43.643766, -79.365934);

    protected GoogleMap map;
    ArrayList<LatLng> markerPoints;
    private HashMap markers;

    private void initMarkers(){
        markers = new HashMap();
        markers.put("Casa Loma Campus", CasaLomaCampus );
        markers.put("Saint James Campus", SaintJamesCampus);
        markers.put("Ryerson University", RyersonUniversity );
        markers.put("Waterfront Campus", WaterfrontCampus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMarkers();

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        final Marker casaLomaCampus = map.addMarker(new MarkerOptions().position(CasaLomaCampus)
                .title("Casa Loma Campus"));
        casaLomaCampus.setSnippet("Casa Loma Campus");
        Marker saintJamesCampus = map.addMarker(new MarkerOptions()
                        .position(SaintJamesCampus)
                        .title("Saint James Campus")
                /*.snippet("Saint James Campus")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher))*/);
        saintJamesCampus.setSnippet("Saint James Campus");
        final Marker ryersonUniversity = map.addMarker(new MarkerOptions().position(RyersonUniversity)
                .title("Ryerson University"));
        ryersonUniversity.setSnippet("Ryerson University Campus");

        Marker waterfrontCampus = map.addMarker(new MarkerOptions().position(WaterfrontCampus)
                .title("Waterfront Campus"));
        waterfrontCampus.setSnippet("Waterfront Campus");

        // Move the camera instantly to hamburg with a zoom of 50.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CasaLomaCampus, 50));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SaintJamesCampus, 50));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(RyersonUniversity, 50));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(WaterfrontCampus, 50));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?f=d&saddr=53.447, -0.878&daddr=51.448, -0.972"));
        intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {

                //Toast.makeText(MainActivity.this, arg0.getTitle(), 1000).show();// display toast
                Intent iSc = new Intent(getApplicationContext(), DisplayLocationDetailsActivity.class);
                iSc.putExtra("ca.gbc.mobile.NatalliaIsayenka.CURRENT_MARKER", arg0.getTitle());

                startActivity(iSc);
                //return true;
            }

        });


        // Enable MyLocation Button in the Map
        map.setMyLocationEnabled(true);


        //startActivity(intent);
    }

    protected void onStart() {
        super.onStart();

        initMarkers();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.setMyLocationEnabled(true);

        String currentMarkerTitle = getIntent().getStringExtra("ca.gbc.mobile.NatalliaIsayenka.DESTINATION_MARKER");

        if ((currentMarkerTitle != null) && !currentMarkerTitle.equalsIgnoreCase("")) {



            LatLng dest  = (LatLng)markers.get(currentMarkerTitle);



            //Location myLoc = map.getMyLocation();
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLoc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            double lat = myLoc.getLatitude();
            double lng = myLoc.getLongitude();
            LatLng currentLocation = new LatLng(lat,
                    lng);

            String url = getDirectionsUrl(currentLocation, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                RouteParser parser = new RouteParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}



