package com.muhammadmustadi.android.modulairclient;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.processbutton.ProcessButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    SessionManager sessionManager;
    public final String baseAPI = "http://modulair.muhammadmustadi.com/v1";


    private LoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView textRes;
    private ActionProcessButton btnGet;
    private InputMethodManager inputManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.textLogin);
        mPasswordView = (EditText) findViewById(R.id.textPass);

        btnGet = (ActionProcessButton) findViewById(R.id.btnGet);
        btnGet.setMode(ActionProcessButton.Mode.ENDLESS);
        textRes = (TextView) findViewById(R.id.textRes);
        inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        String sessionID;
        sessionManager = new SessionManager(getApplicationContext());
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnGet.setProgress(1);
                attemptLogin();
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    inputManager.toggleSoftInput(0, 0);
                    btnGet.setProgress(1);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }
    public void attemptLogin() {
        mEmailView.setEnabled(false);
        mPasswordView.setEnabled(false);
        if (mAuthTask != null) {
            return;
        }
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        textRes.setText("fetching..");
        String url = "http://date.jsontest.com";
        String url2 = "http://modulair.muhammadmustadi.com/v1/subsystems/state";
        String url3 = "http://modulair.muhammadmustadi.com/v1/users/id/551119e9a56115b25c5c8aa8";
        String url4 = "/sessions/login?username="+email+"&password="+password;

        textRes.setText("logging in...");

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mEmailView.setEnabled(true);
            mPasswordView.setEnabled(true);
            focusView.requestFocus();
            btnGet.setProgress(-1);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            new LoginTask().execute(baseAPI+url4);;
            //mAuthTask.execute("http://modulair.muhammadmustadi.com/v1/sessions/login?username="+email+"&password="+password);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
                btnGet.setProgress(-1);
                mEmailView.setEnabled(true);
                mPasswordView.setEnabled(true);
                mPasswordView.setError("check your email/password.");
                mPasswordView.requestFocus();
            }
            if (session!=null) {


                try {

                    btnGet.setProgress(100);
                    String sessionID = session.getString("_id");
                    Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                    sessionManager.createLoginSession(sessionID);
                    i.putExtra("SESSIONID", sessionID);
                    startActivity(i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    mEmailView.setEnabled(true);
                    mPasswordView.setEnabled(true);
                    btnGet.setProgress(-1);
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
