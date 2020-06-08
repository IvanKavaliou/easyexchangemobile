package kavaliou.ivan.net.easyexchangemobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import kavaliou.ivan.net.easyexchangemobile.R;
import kavaliou.ivan.net.easyexchangemobile.model.Transaction;

public class TransactionsListAdapter extends BaseAdapter {

    private LayoutInflater LInflater;
    private ArrayList<Transaction> list;


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

    public TransactionsListAdapter(Context context, ArrayList<Transaction> data){
        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView textAccountCurrency;
        TextView textTransactionType;
        TextView textAmount;
        TextView textDate;

        if ( v == null){
            v = LInflater.inflate(R.layout.transactions_list, parent, false);

        }

        textAccountCurrency = (TextView) v.findViewById(R.id.textAccountCurrency);
        textTransactionType = (TextView) v.findViewById(R.id.textTransactionType);
        textAmount = (TextView) v.findViewById(R.id.textAmount);
        textDate = (TextView) v.findViewById(R.id.textDate);

        Transaction transaction = getTransaction(position);
        if(null == transaction.getAccount()){
            textAccountCurrency.setText("TOP");
        } else {
            textAccountCurrency.setText(transaction.getAccount().getCurrency().name());
        }
        textTransactionType.setText(transaction.getTransaction().name());
        textAmount.setText("Amount: "+transaction.getValue().toString());
        textDate.setText(transaction.getDate().toString());

        return v;
    }

    public Transaction getTransaction(int position){
        return (Transaction) getItem(position);
    }
}
