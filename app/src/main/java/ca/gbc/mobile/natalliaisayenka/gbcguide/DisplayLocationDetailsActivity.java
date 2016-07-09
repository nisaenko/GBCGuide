package ca.gbc.mobile.natalliaisayenka.gbcguide;
/*Natallia Isayenka
* SI 100744884
*created: 04-11-2014
* lastEdit: 17-11-2014
* */
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;


public class DisplayLocationDetailsActivity extends Activity {

    private HashMap imageResources;
    private HashMap descriptionResources;
    String currentMarkerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_location_details);

        imageResources = new HashMap();
        descriptionResources= new HashMap();

        imageResources.put("Casa Loma Campus", new Integer(R.drawable.casa_loma));
        imageResources.put("Saint James Campus", new Integer(R.drawable.saint_james));
        imageResources.put("Ryerson University", new Integer(R.drawable.ryerson));
        imageResources.put("Waterfront Campus", new Integer(R.drawable.waterfront));

        descriptionResources.put("Casa Loma Campus",new Integer(R.string.casa_loma));
        descriptionResources.put("Saint James Campus", new Integer(R.string.saint_james));
        descriptionResources.put("Ryerson University", new Integer(R.string.ryerson));
        descriptionResources.put("Waterfront Campus", new Integer(R.string.waterfront));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_location_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentMarkerTitle = getIntent().getStringExtra("ca.gbc.mobile.NatalliaIsayenka.CURRENT_MARKER");

        ImageView campusView = (ImageView) findViewById(R.id.campusView);

        Drawable new_image= getResources().getDrawable(getImageIDByTitle(currentMarkerTitle));

        campusView.setImageDrawable(new_image);

        EditText campusDescription = (EditText) findViewById(R.id.campusDescription);
        campusDescription.setText(this.getResources().getString(getDescriptionByTitle(currentMarkerTitle)));
    }

    public int getImageIDByTitle(String title){
        return ((Integer) imageResources.get(title)).intValue();
    }

    public int getDescriptionByTitle(String title){
        return ((Integer) descriptionResources.get(title)).intValue();
    }

    public void getDirectionMessage(View view) {
        Intent iSc = new Intent(getApplicationContext(), MainActivity.class);
        iSc.putExtra("ca.gbc.mobile.NatalliaIsayenka.DESTINATION_MARKER", currentMarkerTitle);

        startActivity(iSc);

    }
}
