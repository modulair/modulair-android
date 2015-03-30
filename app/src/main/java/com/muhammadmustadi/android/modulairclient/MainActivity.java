package com.muhammadmustadi.android.modulairclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnGet = (Button) findViewById(R.id.btnGet);
        final TextView textRes = (TextView) findViewById(R.id.textRes);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textRes.setText("fetching..");
                String url = "http://date.jsontest.com";
                String url2 = "http://modulair.muhammadmustadi.com:80/v1/users/id/551119e9a56115b25c5c8aa8";
                new MyAsyncTask().execute(url2);
            }
        });
    }
    class MyAsyncTask extends AsyncTask<String,Void,JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            return RestService.doGet(urls[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            TextView tv = (TextView) findViewById(R.id.textRes);
            tv.setText(jsonObject.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LongRunningGetIO {

    }
}
