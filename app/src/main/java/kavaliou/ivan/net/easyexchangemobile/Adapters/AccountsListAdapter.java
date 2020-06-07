package kavaliou.ivan.net.easyexchangemobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import kavaliou.ivan.net.easyexchangemobile.R;
import kavaliou.ivan.net.easyexchangemobile.model.Account;

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

    public AccountsListAdapter(Context context, ArrayList<Account> data){
        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView textCurrency;
        TextView textBalance;
        Button buttonDelete;

        if ( v == null){
            v = LInflater.inflate(R.layout.accounts_list, parent, false);
            textCurrency = (TextView) v.findViewById(R.id.textCurency);
            textBalance = (TextView) v.findViewById(R.id.textBalance);
            buttonDelete = (Button) v.findViewById(R.id.buttonDelete);

            Account account = getAccount(position);
            textCurrency.setText(account.getCurrency().name());
            textBalance.setText("Balance: "+account.getValue().toString());
        }

        return v;
    }

    public Account getAccount(int position){
        return (Account) getItem(position);
    }
}
