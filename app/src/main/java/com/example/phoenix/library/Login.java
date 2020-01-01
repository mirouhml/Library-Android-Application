package com.example.phoenix.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.phoenix.library.Helper.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    private String TAG = Login.class.getSimpleName();
    String emailText;
    String passwordText;
    String ok = "false";
    static final String ipAddress = "192.168.43.107";
    String jsonStr = null;
    String type;
    View loginView;
    public static SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginView = findViewById(R.id.loginView);
        final Button login = findViewById(R.id.loginButton);
        final TextView signup = findViewById(R.id.sign_up);
        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);
        String email = sharedpreferences.getString("LOGIN", null);
        if (email != null) {
            Intent intent = new Intent(Login.this,home.class);
            intent.putExtra("EMAIL", email);
            startActivity(intent);
            finish();
        }else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText email = findViewById(R.id.email);
                    EditText password = findViewById(R.id.password);
                    if (validate(email,password)) {
                        emailText = email.getText().toString();
                        passwordText = password.getText().toString();
                        new GetLogin().execute();
                    }
                    else{
                        Snackbar.make(loginView, "Login Failed.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this,Signup.class);
                    startActivity(intent);
                }
            });
        }
        TextView forgotPassword = findViewById(R.id.forgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Reset_Password.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private class GetLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "http://"+ipAddress+":9999/LibraryBackgroundService.csp/login/"+emailText+"/"+passwordText;
            // Making a request to url and getting response
            jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONObject books = new JSONObject(jsonStr);
                    ok = books.getString("loginResult");
                    url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/isEtudiant/"+emailText;
                    jsonStr = sh.makeServiceCall(url);
                    books = new JSONObject(jsonStr);
                    type = books.getString("isEtudiantResult");
                    Log.e(TAG, "ok: " + ok);

                } catch (final JSONException e) {
                    Log.e(TAG, "Response from url: " + e);
                }
            }
            else{
                Snackbar.make(loginView, "There was a problem retrieving the data.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e(TAG, "PostExecute ");
            if (jsonStr != null){
                if (ok.equals("true")){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("LOGIN", emailText);
                    if (type.equals("true"))
                        editor.putString("TYPE","Student");
                    else editor.putString("TYPE","Teacher");

                    editor.apply();
                    final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                            ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();

                    Intent intent = new Intent(Login.this,home.class);
                    intent.putExtra("EMAIL", emailText);
                    startActivity(intent);
                    finish();
                }
                else {

                    Snackbar.make(loginView, "The information you entered are wrong.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

        }

    }
    private boolean validate(EditText _emailText,EditText _passwordText) {
        boolean valid = true;
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address please.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Enter your password please.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
