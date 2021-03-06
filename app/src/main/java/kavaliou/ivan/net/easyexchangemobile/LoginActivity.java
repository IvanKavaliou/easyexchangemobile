package kavaliou.ivan.net.easyexchangemobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import kavaliou.ivan.net.easyexchangemobile.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordRepeat;
    private Button buttonSignIn;
    private CheckBox checkRegister;
    private TextView errorTextView;
    private ImageView imageLoginLogo;

    private static String URL_LOGIN = "http://192.168.0.101:8080/login";
    private static String URL_REGISTRATION = "http://192.168.0.101:8080/registration";

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        initView();
    }

    public void initView(){
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPasswordRepeat = (EditText) findViewById(R.id.editPasswordRepeat);
        errorTextView = (TextView) findViewById(R.id.errorTextView);

        imageLoginLogo = (ImageView) findViewById(R.id.imageLoginLogo);
        imageLoginLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmail.setText("user@user.com");
                editPassword.setText("user");
            }
        });

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!editEmail.getText().toString().trim().isEmpty()){
                        if (!editPassword.getText().toString().isEmpty()){
                            if (!checkRegister.isChecked()){
                                login();
                                errorTextView.setText("");
                            } else {
                                if (editPassword.getText().toString().equals(editPasswordRepeat.getText().toString())){
                                    regisrtation();
                                    errorTextView.setText("");
                                } else {
                                    errorTextView.setText("Passwords can be equals!");
                                }
                            }
                        } else {
                            errorTextView.setText("Password cannot be emprt!");
                        }
                    } else {
                        errorTextView.setText("Email canot be empty!");
                    }
                queue.start();
            }
        });


        checkRegister = (CheckBox) findViewById(R.id.checkRegister);
        checkRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   if (isChecked){
                       editPasswordRepeat.setVisibility(View.VISIBLE);
                       buttonSignIn.setText(getString(R.string.sign_up));
                   } else {
                       editPasswordRepeat.setVisibility(View.INVISIBLE);
                       buttonSignIn.setText(getString(R.string.sign_in));
                   }
                }
         });
    }

    private void startMainActivity(User user){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    private void regisrtation() {
        Map<String, String> params = new HashMap();
        params.put("email", editEmail.getText().toString().trim().toLowerCase());
        params.put("password", editPassword.getText().toString());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_REGISTRATION, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                User user = gson.fromJson(response.toString(),User.class);
                //errorTextView.setText(user.toString());
                startMainActivity(user);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(null != error.networkResponse && error.networkResponse.data!=null) {
                    try {
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        String body = new String(error.networkResponse.data,"UTF-8");
                        try {
                            JSONObject jsonError = new JSONObject(body);
                            if (statusCode.equals("406") || statusCode.equals("404")){
                                errorTextView.setText(jsonError.getString("message"));
                            } else {
                                JSONArray errors = jsonError.getJSONArray("errors");
                                errorTextView.setText("");
                                for (int i = 0; i < errors.length(); i++){
                                    errorTextView.setText(errorTextView.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            errorTextView.setText(e.getMessage());
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        errorTextView.setText(e.getMessage());
                    }
                } else {
                    errorTextView.setText("Service is unavailable :(");
                }
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void login(){
        Map<String, String> params = new HashMap();
        params.put("email", editEmail.getText().toString().trim().toLowerCase());
        params.put("password", editPassword.getText().toString());
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(response.toString(),User.class);
                    //errorTextView.setText(user.toString());
                    startMainActivity(user);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(null != error.networkResponse && error.networkResponse.data!=null) {
                    try {
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        String body = new String(error.networkResponse.data,"UTF-8");
                        try {
                            JSONObject jsonError = new JSONObject(body);
                            if (statusCode.equals("406") || statusCode.equals("404")){
                                errorTextView.setText(jsonError.getString("message"));
                            } else {
                                JSONArray errors = jsonError.getJSONArray("errors");
                                errorTextView.setText("");
                                for (int i = 0; i < errors.length(); i++){
                                    errorTextView.setText(errorTextView.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            errorTextView.setText(e.getMessage());
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        errorTextView.setText(e.getMessage());
                    }
                } else {
                    errorTextView.setText("Service is unavailable :(");
                }
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }


}
