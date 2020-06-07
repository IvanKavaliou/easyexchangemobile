package kavaliou.ivan.net.easyexchangemobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import kavaliou.ivan.net.easyexchangemobile.Adapters.AccountsListAdapter;
import kavaliou.ivan.net.easyexchangemobile.Adapters.TransactionsListAdapter;
import kavaliou.ivan.net.easyexchangemobile.model.Account;
import kavaliou.ivan.net.easyexchangemobile.model.Transaction;
import kavaliou.ivan.net.easyexchangemobile.model.User;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.CurrencyType;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.TransactionType;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private ListView accountsList;
    private ListView transactionsList;

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

        //Accounts List Initialization
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(Account.builder().id(1).currency(CurrencyType.EUR).value(BigDecimal.TEN).build());
        accounts.add(Account.builder().id(2).currency(CurrencyType.USD).value(BigDecimal.ZERO).build());
        accounts.add(Account.builder().id(3).currency(CurrencyType.PLN).value(BigDecimal.ONE).build());
        AccountsListAdapter accountsListAdapter = new AccountsListAdapter(this, accounts);
        accountsList = (ListView) findViewById(R.id.accounts_list);
        accountsList.setAdapter(accountsListAdapter);

        //Transaction List Initialization
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(Transaction.builder().account(accounts.get(1)).value(BigDecimal.TEN).date(new Date()).transaction(TransactionType.TOP_UP).build());
        transactions.add(Transaction.builder().account(accounts.get(2)).value(BigDecimal.ONE).date(new Date()).transaction(TransactionType.EXCHANGE).build());
        TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(this, transactions);
        transactionsList = (ListView) findViewById(R.id.transactions_list);
        transactionsList.setAdapter(transactionsListAdapter);
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
        } else if (id == R.id.nav_transactions) {
            accountsList.setVisibility(View.INVISIBLE);
            transactionsList.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_currency_rates) {

        } else if (id == R.id.nav_top_up) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
