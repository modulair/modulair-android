package com.muhammadmustadi.android.modulairclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    SessionManager sessionManager;
    public final String baseAPI = "http://modulair.muhammadmustadi.com/v1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText etLogin = (EditText) findViewById(R.id.textLogin);
        final EditText etPass = (EditText) findViewById(R.id.textPass);

        final Button btnGet = (Button) findViewById(R.id.btnGet);
        final TextView textRes = (TextView) findViewById(R.id.textRes);

        String sessionID;
        sessionManager = new SessionManager(getApplicationContext());
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textRes.setText("fetching..");
                String url = "http://date.jsontest.com";
                String url2 = "http://modulair.muhammadmustadi.com/v1/subsystems/state";
                String url3 = "http://modulair.muhammadmustadi.com/v1/users/id/551119e9a56115b25c5c8aa8";
                String url4 = "/sessions/login?username="+etLogin.getText()+"&password="+etPass.getText();

                textRes.setText("logging in...");
                new LoginTask().execute(baseAPI+url4);
            }
        });
    }

    class LoginTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            return RestService.doPost(urls[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            TextView tv = (TextView) findViewById(R.id.textRes);
            tv.setText(jsonObject.toString());
            JSONObject session = null;
            try {
                session = jsonObject.getJSONObject("session");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (session!=null) {
                try {
                    String sessionID = session.getString("_id");
                    Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                    sessionManager.createLoginSession(sessionID);
                    i.putExtra("SESSIONID", sessionID);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv.setText("Please try again.");
                }
            }
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
