package kavaliou.ivan.net.easyexchangemobile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import kavaliou.ivan.net.easyexchangemobile.Adapters.AccountsListAdapter;
import kavaliou.ivan.net.easyexchangemobile.Adapters.CurrencysRatesListAdapter;
import kavaliou.ivan.net.easyexchangemobile.Adapters.TransactionsListAdapter;
import kavaliou.ivan.net.easyexchangemobile.model.Account;
import kavaliou.ivan.net.easyexchangemobile.model.CurrencyRate;
import kavaliou.ivan.net.easyexchangemobile.model.TopUp;
import kavaliou.ivan.net.easyexchangemobile.model.Transaction;
import kavaliou.ivan.net.easyexchangemobile.model.User;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private ListView accountsList;
    private ListView transactionsList;
    private ListView currencyRatesList;
    private LinearLayout topupLayout;

    private  ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;
    private ArrayList<CurrencyRate> currencyRates;

    private TextView textErrors;

    private RequestQueue queue;

    private static final String URL_GET_ACCOUNTS ="http://192.168.0.101:8080/accounts";
    private static final String URL_GET_TRANSACTIONS ="http://192.168.0.101:8080/transactions";
    private static final String URL_GET_RATES ="http://192.168.0.101:8080/rates";
    private static final String URL_TOP_UP = "http://192.168.0.101:8080/topup";
    private static final String URL_ADD_ACCOUNT = "http://192.168.0.101:8080/accounts/add/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LinearLayout addAccountLayout = (LinearLayout) findViewById(R.id.addAccountLayout);
               if (addAccountLayout.getVisibility() == View.GONE){
                   addAccountLayout.setVisibility(View.VISIBLE);
               } else {
                   addAccountLayout.setVisibility(View.GONE);
               }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textErrors = (TextView) findViewById(R.id.textErrors);

        initUser();

        //Accounts List Initialization
        queue = Volley.newRequestQueue(this);
        accountsList = (ListView) findViewById(R.id.accounts_list);
        initAccounts();

        //Transaction List Initialization
        transactionsList = (ListView) findViewById(R.id.transactions_list);
        initTransactions();

        //Top UP Layout Initialization
        initTopUP();

        //Currency Rates List Initialization
        currencyRatesList = (ListView) findViewById(R.id.currecny_rates_list);
        initCurrencyRates();

        queue.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            update();
        }
    }

    private void update(){
        initUser();
        initAccounts();
        initTransactions();
        initTopUP();
        queue.start();
    }

    private void initTopUP(){
        final EditText editTextTopUpValue = (EditText) findViewById(R.id.editTextTopUpValue);
        topupLayout = (LinearLayout) findViewById(R.id.topupLayout);
        TextView textBalance = (TextView) findViewById(R.id.textBalanceTopUp);
        textBalance.setText("Balance: " + user.getBalance()+ " PLN");
        Button buttonTopUp = (Button) findViewById(R.id.buttonTopUp);
        buttonTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopUp t = new TopUp();
                t.setValue(BigDecimal.valueOf(Double.valueOf(editTextTopUpValue.getText().toString())));
                topUp(t);
            }
        });
    }

    private void topUp(final TopUp topUp){
        final Context context = this;
        Map<String, String> params = new HashMap();
        params.put("value", topUp.getValue().toString());
        JSONObject parameters = new JSONObject(params);
        //QUEEE
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, URL_TOP_UP, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        TopUp t  = gson.fromJson(response.toString(),TopUp.class);
                        user.setBalance(t.getValue());
                        update();
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
                                            textErrors.setText(jsonError.getString("message"));
                                        } else {
                                            JSONArray errors = jsonError.getJSONArray("errors");
                                            textErrors.setText("");
                                            for (int i = 0; i < errors.length(); i++){
                                                textErrors.setText(textErrors.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                            }
                                        }
                                        textErrors.setVisibility(View.VISIBLE);
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

    private void initUser(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        user = (User) getIntent().getSerializableExtra("user");
        View headerView = navigationView.getHeaderView(0);
        TextView textViewEmail = (TextView) headerView.findViewById(R.id.textViewEmail);
        TextView textMenueBalance = (TextView) headerView.findViewById(R.id.textMenueBalance);
        textMenueBalance.setText("Balance: " + user.getBalance().toString() + " PLN");
        textViewEmail.setText(user.getEmail());
    }

    private void initCurrencyRates(){
        currencyRates = new ArrayList<>();
        final Context context = this;
        //QUEEE
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_RATES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                CurrencyRate currencyRate =  gson.fromJson(response.getJSONObject(i).toString(),CurrencyRate.class);
                                currencyRates.add(currencyRate);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        CurrencysRatesListAdapter currencysRatesListAdapter = new CurrencysRatesListAdapter(context, currencyRates);
                        currencyRatesList.setAdapter(currencysRatesListAdapter);
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
                                            textErrors.setText(jsonError.getString("message"));
                                        } else {
                                            JSONArray errors = jsonError.getJSONArray("errors");
                                            textErrors.setText("");
                                            for (int i = 0; i < errors.length(); i++){
                                                textErrors.setText(textErrors.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                            }
                                        }
                                        currencyRatesList.setVisibility(View.GONE);
                                        textErrors.setVisibility(View.VISIBLE);
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

    private void initAddAccounts(){
        AccountsListAdapter accountsListAdapter = new AccountsListAdapter(this, accounts, user);
        accountsList.setAdapter(accountsListAdapter);
        String[] currencys = new String[CurrencyType.values().length];
        ArrayList<CurrencyType> cts = new ArrayList<>();
        for (Account a : accounts){
            cts.add(a.getCurrency());
        }
        int i = 0;
        for (CurrencyType c:CurrencyType.values()){
            if (!cts.contains(c)){
                currencys[i++] = c.name();
            }
        }
        final String[] curTrans = new String[i];
        for (int t=0; t < i; t++){
            curTrans[t]=currencys[t];
        }
        final ListView accountAddList = (ListView) findViewById(R.id.accountAddList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_single_choice,curTrans);
        accountAddList.setAdapter(adapter);
        Button buttonAccountAdd = (Button) findViewById(R.id.buttonAccountAdd);
        buttonAccountAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount(CurrencyType.valueOf(curTrans[accountAddList.getCheckedItemPosition()]));
            }
        });
    }

    private void addAccount(CurrencyType currencyType){
        final Context context = this;
        //QUEEE
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.GET, URL_ADD_ACCOUNT + currencyType.name(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        update();
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
                                            textErrors.setText(jsonError.getString("message"));
                                        } else {
                                            JSONArray errors = jsonError.getJSONArray("errors");
                                            textErrors.setText("");
                                            for (int i = 0; i < errors.length(); i++){
                                                textErrors.setText(textErrors.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                            }
                                        }
                                        textErrors.setVisibility(View.VISIBLE);
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

    private void initAccounts(){
        accounts = new ArrayList<>();
        final Context context = this;
        //QUEEE
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_ACCOUNTS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                Account account =  gson.fromJson(response.getJSONObject(i).toString(),Account.class);
                                accounts.add(account);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        initAddAccounts();
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
                                            textErrors.setText(jsonError.getString("message"));
                                        } else {
                                            JSONArray errors = jsonError.getJSONArray("errors");
                                            textErrors.setText("");
                                            for (int i = 0; i < errors.length(); i++){
                                                textErrors.setText(textErrors.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                            }
                                        }
                                        textErrors.setVisibility(View.VISIBLE);
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

    private void initTransactions(){
        transactions = new ArrayList<>();
        final Context context = this;

        //QUEEE
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_TRANSACTIONS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                Transaction transaction =  gson.fromJson(response.getJSONObject(i).toString(),Transaction.class);
                                transactions.add(transaction);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(context, transactions);
                        transactionsList.setAdapter(transactionsListAdapter);
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
                                            textErrors.setText(jsonError.getString("message"));
                                        } else {
                                            JSONArray errors = jsonError.getJSONArray("errors");
                                            textErrors.setText("");
                                            for (int i = 0; i < errors.length(); i++){
                                                textErrors.setText(textErrors.getText() + errors.get(i).toString() + System.getProperty("line.separator"));
                                            }
                                        }
                                        textErrors.setVisibility(View.VISIBLE);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        textErrors.setVisibility(View.GONE);
        if (id == R.id.nav_accounts) {
            initAccounts();
            queue.start();
            accountsList.setVisibility(View.VISIBLE);
            transactionsList.setVisibility(View.GONE);
            topupLayout.setVisibility(View.GONE);
            currencyRatesList.setVisibility(View.GONE);
        } else if (id == R.id.nav_transactions) {
            initTransactions();
            queue.start();
            transactionsList.setVisibility(View.VISIBLE);
            accountsList.setVisibility(View.GONE);
            topupLayout.setVisibility(View.GONE);
            currencyRatesList.setVisibility(View.GONE);
        } else if (id == R.id.nav_currency_rates) {
            initCurrencyRates();
            queue.start();
            currencyRatesList.setVisibility(View.VISIBLE);
            topupLayout.setVisibility(View.GONE);
            accountsList.setVisibility(View.GONE);
            transactionsList.setVisibility(View.GONE);
        } else if (id == R.id.nav_top_up) {
            initTopUP();
            queue.start();
            topupLayout.setVisibility(View.VISIBLE);
            accountsList.setVisibility(View.GONE);
            transactionsList.setVisibility(View.GONE);
            currencyRatesList.setVisibility(View.GONE);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
