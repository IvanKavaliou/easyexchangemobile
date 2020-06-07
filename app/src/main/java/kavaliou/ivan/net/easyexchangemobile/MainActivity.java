package kavaliou.ivan.net.easyexchangemobile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kavaliou.ivan.net.easyexchangemobile.Adapters.AccountsListAdapter;
import kavaliou.ivan.net.easyexchangemobile.Adapters.CurrencysRatesListAdapter;
import kavaliou.ivan.net.easyexchangemobile.Adapters.TransactionsListAdapter;
import kavaliou.ivan.net.easyexchangemobile.model.Account;
import kavaliou.ivan.net.easyexchangemobile.model.CurrencyRate;
import kavaliou.ivan.net.easyexchangemobile.model.Transaction;
import kavaliou.ivan.net.easyexchangemobile.model.User;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.TransactionType;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private ListView accountsList;
    private ListView transactionsList;
    private ListView currencyRatesList;
    private LinearLayout topupLayout;

    private  ArrayList<Account> accounts;
    ArrayList<Transaction> transactions;

    private TextView textErrors;

    private RequestQueue queue;

    private static final String URL_GET_ACCOUNTS ="http://192.168.0.101:8080/accounts";
    private static final String URL_GET_TRANSACTIONS ="http://192.168.0.101:8080/transactions";


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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = (User) getIntent().getSerializableExtra("user");
        View headerView = navigationView.getHeaderView(0);
        TextView textViewEmail = (TextView) headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(user.getEmail());

        textErrors = (TextView) findViewById(R.id.textErrors);

        //Accounts List Initialization
        queue = Volley.newRequestQueue(this);
        initAccounts();

        //Transaction List Initialization
        initTransactions();
        queue.start();

        //Top UP Layout Initialization
        topupLayout = (LinearLayout) findViewById(R.id.topupLayout);
        TextView textBalance = (TextView) findViewById(R.id.textBalance);
        textBalance.setText("Balance: " + user.getBalance());

        //Currency Rates List Initialization
        ArrayList<CurrencyRate> currencyRates = new ArrayList<>();
        currencyRates.add(CurrencyRate.builder().currency("dollar amerikanski").code(CurrencyType.USD).bid(BigDecimal.valueOf(4.23)).ask(BigDecimal.valueOf(4.11)).build());
        currencyRates.add(CurrencyRate.builder().currency("euro").code(CurrencyType.EUR).bid(BigDecimal.valueOf(4.43)).ask(BigDecimal.valueOf(4.21)).build());
        CurrencysRatesListAdapter currencysRatesListAdapter = new CurrencysRatesListAdapter(this, currencyRates);
        currencyRatesList = (ListView) findViewById(R.id.currecny_rates_list);
        currencyRatesList.setAdapter(currencysRatesListAdapter);
    }

    private void initAccounts(){
        accounts = new ArrayList<>();
        final Context context = this;
        //QUEEE
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_ACCOUNTS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        textErrors.setText(response.toString());

                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                Account account =  gson.fromJson(response.getJSONObject(i).toString(),Account.class);
                                accounts.add(account);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        AccountsListAdapter accountsListAdapter = new AccountsListAdapter(context, accounts);
                        accountsList = (ListView) findViewById(R.id.accounts_list);
                        accountsList.setAdapter(accountsListAdapter);
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
                        textErrors.setText(response.toString());

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
                        transactionsList = (ListView) findViewById(R.id.transactions_list);
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

        if (id == R.id.nav_accounts) {
            accountsList.setVisibility(View.VISIBLE);
            transactionsList.setVisibility(View.INVISIBLE);
            topupLayout.setVisibility(View.INVISIBLE);
            currencyRatesList.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_transactions) {
            transactionsList.setVisibility(View.VISIBLE);
            accountsList.setVisibility(View.INVISIBLE);
            topupLayout.setVisibility(View.INVISIBLE);
            currencyRatesList.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_currency_rates) {
            currencyRatesList.setVisibility(View.VISIBLE);
            topupLayout.setVisibility(View.INVISIBLE);
            accountsList.setVisibility(View.INVISIBLE);
            transactionsList.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_top_up) {
            topupLayout.setVisibility(View.VISIBLE);
            accountsList.setVisibility(View.INVISIBLE);
            transactionsList.setVisibility(View.INVISIBLE);
            currencyRatesList.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
