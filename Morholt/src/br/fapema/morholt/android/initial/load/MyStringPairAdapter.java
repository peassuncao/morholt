package br.fapema.morholt.android.initial.load;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import br.fapema.morholt.android.wizardpager.wizard.basic.TwoColumnListViewInterface;

import br.fapema.morholt.android.R;

public class MyStringPairAdapter extends BaseAdapter {
	private Context context;
    private Activity fragment;
    private List<TwoColumnListViewInterface> stringPairList;
    

    public MyStringPairAdapter(Context context, Activity activity, List<TwoColumnListViewInterface> stringPairList) {
        super();
        this.context = context;
        this.fragment = activity;
        this.stringPairList = stringPairList;
    }

    @Override
    public int getCount() {
        return stringPairList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringPairList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
        	LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.initial_load_listrow, null);
        }
        TextView col1 = (TextView) convertView.findViewById(R.id.column1);
        TextView col2 = (TextView) convertView.findViewById(R.id.column2);
        
        col1.setText(stringPairList.get(position).getColumnOne());
        col2.setText(stringPairList.get(position).getColumnTwo());
        
        return convertView;
    }
}
