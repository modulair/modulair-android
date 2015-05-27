package com.muhammadmustadi.android.modulairclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.cengalabs.flatui.views.FlatToggleButton;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.HashMap;



public class DashboardActivity extends ActionBarActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://modulair.muhammadmustadi.com");
        } catch (URISyntaxException e) {}
    }
    SessionManager session;

//    private Emitter.Listener androidCamNotifier = new Emitter.Listener() {
//        @Override
//        public void call(final Object.. args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try{
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }
//
//                    // add the message to view
//                    //addMessage(username, message);
//                }
//            });
//        }
//    };
    private void createNotification(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Modulair notification")
                        .setContentText(message);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, DashboardActivity.class);
        // The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DashboardActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSocket.on("androidcam", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String messageCam;
                        try {
                            messageCam = data.getString("message");
                        } catch (JSONException e) {
                            return;
                        }
                        Log.v("camera", messageCam);
                        createNotification(messageCam);
                    }
                });
            }
        });

        mSocket.connect();

        Intent intent = getIntent();
        String message = intent.getStringExtra("SESSIONID");
        setContentView(R.layout.activity_dashboard);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        if (message==null) {
            // name
            message = user.get(SessionManager.KEY_SESSION);
        }
        FlatToggleButton sw1 = (FlatToggleButton) findViewById(R.id.switch1);
        FlatToggleButton sw2 = (FlatToggleButton) findViewById(R.id.switch2);
        FlatToggleButton sw3 = (FlatToggleButton) findViewById(R.id.switch3);
        FlatToggleButton sw4 = (FlatToggleButton) findViewById(R.id.switch4);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        TextView tvDash = (TextView) findViewById(R.id.textViewID);
        tvDash.setText("hello " + message);
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new ToggleTask().execute("http://modulair.muhammadmustadi.com/v1/subsystems/55113c458302d32802674d8c/toggle/1");
            }
        });
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new ToggleTask().execute("http://modulair.muhammadmustadi.com/v1/subsystems/55113c458302d32802674d8c/toggle/2");
            }
        });
        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new ToggleTask().execute("http://modulair.muhammadmustadi.com/v1/subsystems/55113c458302d32802674d8c/toggle/3");
            }
        });
        sw4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new ToggleTask().execute("http://modulair.muhammadmustadi.com/v1/subsystems/55113c458302d32802674d8c/toggle/4");
            }
        });
        /**
         * Logout button click event
         * */
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });
    }

    class ToggleTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            return RestService.doPost(urls[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("androidcam");

    }
}
