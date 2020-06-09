package kavaliou.ivan.net.easyexchangemobile.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import kavaliou.ivan.net.easyexchangemobile.MainActivity;
import kavaliou.ivan.net.easyexchangemobile.R;
import kavaliou.ivan.net.easyexchangemobile.TransactionActivity;
import kavaliou.ivan.net.easyexchangemobile.model.Account;
import kavaliou.ivan.net.easyexchangemobile.model.User;
import kavaliou.ivan.net.easyexchangemobile.utils.enums.TransactionType;

public class AccountsListAdapter extends BaseAdapter {

    private LayoutInflater LInflater;
    private ArrayList<Account> list;


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private MainActivity context;

    private User user;

    public AccountsListAdapter(MainActivity context, ArrayList<Account> data, User user){
        this.user = user;
        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView textCurrency;
        TextView textBalance;
        final Button buttonBuy;
        Button buttonSell;

        if ( v == null){
            v = LInflater.inflate(R.layout.accounts_list, parent, false);
        }
            final Account account = getAccount(position);
            textCurrency = (TextView) v.findViewById(R.id.textCurency);
            textBalance = (TextView) v.findViewById(R.id.textBalance);

            buttonBuy = (Button) v.findViewById(R.id.buttonBuy);
            buttonBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTransactionActivity(TransactionType.BUY, account);
                }
            });

            buttonSell = (Button) v.findViewById(R.id.buttonSell);
            buttonSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTransactionActivity(TransactionType.SELL, account);
                }
            });
            textCurrency.setText(account.getCurrency().name());
            textBalance.setText("Balance: "+account.getValue().toString());


        return v;
    }

    private void openTransactionActivity(TransactionType transactionType, Account account){
        Intent i = new Intent(context,TransactionActivity.class);
        i.putExtra("account", account);
        i.putExtra("transactionType",transactionType);
        i.putExtra("user",user);
        context.startActivityForResult(i, 100);
    }

    public Account getAccount(int position){
        return (Account) getItem(position);
    }
}
