package kavaliou.ivan.net.easyexchangemobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
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

import kavaliou.ivan.net.easyexchangemobile.model.Account;
import kavaliou.ivan.net.easyexchangemobile.model.CurrencyRate;
import kavaliou.ivan.net.easyexchangemobile.model.User;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.TransactionType;

public class TransactionActivity extends AppCompatActivity {

    private Account account;
    private TransactionType transactionType;
    private RequestQueue queue;

    private static final String RATE_URL = "http://192.168.0.101:8080/rates/";

    private TextView textErrorTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        textErrorTransaction = (TextView) findViewById(R.id.textErrorTransaction);

        queue = Volley.newRequestQueue(this);

        account = (Account) getIntent().getSerializableExtra("account");
        transactionType = (TransactionType) getIntent().getSerializableExtra("transactionType");

        init();
        queue.start();
    }

    private void init(){
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
                buttonAction.setText(transactionType.name());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
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
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }
}
