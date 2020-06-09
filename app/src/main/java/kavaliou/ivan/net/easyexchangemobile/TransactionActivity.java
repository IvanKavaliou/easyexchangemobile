package kavaliou.ivan.net.easyexchangemobile;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import kavaliou.ivan.net.easyexchangemobile.model.Account;
import kavaliou.ivan.net.easyexchangemobile.model.CurrencyRate;
import kavaliou.ivan.net.easyexchangemobile.model.OperationDTO;
import kavaliou.ivan.net.easyexchangemobile.model.User;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.TransactionType;

public class TransactionActivity extends AppCompatActivity {

    private Account account;
    private TransactionType transactionType;
    private User user;
    private RequestQueue queue;

    private static final String RATE_URL = "http://192.168.0.101:8080/rates/";
    private static final String RATE_TRANS = "http://192.168.0.101:8080/";

    private TextView textErrorTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        textErrorTransaction = (TextView) findViewById(R.id.textErrorTransaction);

        queue = Volley.newRequestQueue(this);

        account = (Account) getIntent().getSerializableExtra("account");
        transactionType = (TransactionType) getIntent().getSerializableExtra("transactionType");
        user = (User) getIntent().getSerializableExtra("user");
        Button buttonAction = (Button) findViewById(R.id.buttonAction);
        buttonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trans();
            }
        });

        init();
        queue.start();
    }

    private void trans(){
        EditText editValue = (EditText) findViewById(R.id.editValue);
        Map<String, String> params = new HashMap();
        params.put("currency", account.getCurrency().name());
        params.put("value", editValue.getText().toString());
        JSONObject parameters = new JSONObject(params);

        final Context context = this;

        //QUEEE
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, RATE_TRANS + transactionType.name().toLowerCase(), parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setResult(RESULT_OK);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body;
                        if (error.networkResponse != null){
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            if(error.networkResponse.data!=null) {
                                try {
                                    body = new String(error.networkResponse.data,"UTF-8");
                                    try {
                                        JSONObject jsonError = new JSONObject(body);
                                        if (statusCode.equals("406") || statusCode.equals("404")){
                                            textErrorTransaction.setText(jsonError.getString("message"));
                                        } else {
                                            JSONArray errors = jsonError.getJSONArray("errors");
                                            textErrorTransaction.setText("");
                                            for (int i = 0; i < errors.length(); i++){
                                                textErrorTransaction.setText(textErrorTransaction.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                            }
                                        }
                                        textErrorTransaction.setVisibility(View.VISIBLE);
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",user.getEmail(),user.getPassword());
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(jRequest);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    private void init(){
        Button buttonAction = (Button) findViewById(R.id.buttonAction);
        buttonAction.setText(transactionType.name());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, RATE_URL+account.getCurrency().name(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                CurrencyRate currencyRate = gson.fromJson(response.toString(), CurrencyRate.class);

                TextView textCurrencyCode = (TextView) findViewById(R.id.textCurrencyCode);
                TextView textBid = (TextView) findViewById(R.id.textBid);
                TextView textAsk = (TextView) findViewById(R.id.textAsk);
                TextView textCurrencyName = (TextView) findViewById(R.id.textCurrencyName);
                Button buttonAction = (Button) findViewById(R.id.buttonAction);

                textCurrencyCode.setText(currencyRate.getCode().name());
                textBid.setText("Bid: " + currencyRate.getBid().toString());
                textAsk.setText("Ask: " + currencyRate.getAsk().toString());
                textCurrencyName.setText(currencyRate.getCurrency());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                String statusCode;
                if(null != error.networkResponse && error.networkResponse.data!=null) {
                    try {
                        statusCode = String.valueOf(error.networkResponse.statusCode);
                        body = new String(error.networkResponse.data,"UTF-8");
                        try {
                            JSONObject jsonError = new JSONObject(body);
                            if (statusCode.equals("406") || statusCode.equals("404")){
                                textErrorTransaction.setText(jsonError.getString("message"));
                            } else {
                                JSONArray errors = jsonError.getJSONArray("errors");
                                textErrorTransaction.setText("");
                                for (int i = 0; i < errors.length(); i++){
                                    textErrorTransaction.setText(textErrorTransaction.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            textErrorTransaction.setText(e.getMessage());
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        textErrorTransaction.setText(e.getMessage());
                    }
                }
                textErrorTransaction.setVisibility(View.VISIBLE);
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }
}
