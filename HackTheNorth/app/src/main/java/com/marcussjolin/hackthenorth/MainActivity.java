package com.marcussjolin.hackthenorth;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.PerformanceTestCase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button capture_button = (Button) findViewById(R.id.capture);
        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewVideoActivity.class);
                startActivity(intent);
            }
        });

        Button logout_button = (Button) findViewById(R.id.logout);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.logOut();
                Intent intent = new Intent(mContext, InitialActivity.class);
                startActivity(intent);
            }
        });
        ParseQuery query = new ParseQuery("video");
        query.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        List<ParseObject> dataHolder = null;
        try {
            dataHolder = query.find();
        } catch (ParseException e) {
            Log.d("TAG", e.getMessage());
        }
        final List<ParseObject> dataHolderFinal = dataHolder;
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mContext, ViewVideoActivity.class);
                intent.putExtra(
                        "video", dataHolderFinal.get(position).getParseFile("video").getUrl());
                startActivity(intent);
            }
        });
        ArrayAdapter<ParseObject> objectArrayAdapter = new MyPerformanceArrayAdapter(
                this, dataHolder);
        setListAdapter(objectArrayAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyPerformanceArrayAdapter extends ArrayAdapter<ParseObject> {
        private final Activity context;
        private final List<ParseObject> names;

        class ViewHolder {
            public TextView text;
        }

        public MyPerformanceArrayAdapter(Activity context, List<ParseObject> names) {
            super(context, R.layout.rowlayout, names);
            this.context = context;
            this.names = names;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.rowlayout, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                // TODO: Fix the TextView text values.
                viewHolder.text = (TextView) rowView.findViewById(R.id.label);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            String s = names.get(position).getString("sender");
            holder.text.setText(s);
            return rowView;
        }
    }
}
