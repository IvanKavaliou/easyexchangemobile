package kavaliou.ivan.net.easyexchangemobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kavaliou.ivan.net.easyexchangemobile.R;
import kavaliou.ivan.net.easyexchangemobile.model.CurrencyRate;

public class CurrencysRatesListAdapter extends BaseAdapter {
    private LayoutInflater LInflater;
    private ArrayList<CurrencyRate> list;


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

    public CurrencysRatesListAdapter(Context context, ArrayList<CurrencyRate> data){
        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView textCurrencyCode;
        TextView textBid;
        TextView textAsk;
        TextView textCurrencyName;

        if ( v == null){
            v = LInflater.inflate(R.layout.currencys_rates_list, parent, false);
            textCurrencyCode = (TextView) v.findViewById(R.id.textCurrencyCode);
            textBid = (TextView) v.findViewById(R.id.textBid);
            textAsk = (TextView) v.findViewById(R.id.textAsk);
            textCurrencyName = (TextView) v.findViewById(R.id.textCurrencyName);

            CurrencyRate currencyRate = getCurrencyRate(position);
            textCurrencyCode.setText(currencyRate.getCode().name());
            textBid.setText("Bid: " + currencyRate.getBid().toString());
            textAsk.setText("Ask: " + currencyRate.getAsk().toString());
            textCurrencyName.setText(currencyRate.getCurrency());
        }

        return v;
    }

    public CurrencyRate getCurrencyRate(int position){
        return (CurrencyRate) getItem(position);
    }
}
