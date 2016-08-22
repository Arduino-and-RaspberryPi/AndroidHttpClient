package net.simplifiedcoding.sqlitedbcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private Button addNew;
    ServerCustomAdapter serverAdapter;
    ListView serverList;

    private SQLiteDatabase db;

    ArrayList<Server> serverArray = new ArrayList<Server>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDatabase();

        addNew = (Button) findViewById(R.id.addNew);
        addNew.setOnClickListener(this);

        serverArray.add(new Server("Mumer", "12.34.56", "90"));
        serverArray.add(new Server("Jon", "12.45.23.90", "9000"));
        serverArray.add(new Server("Broom", "09.231.12", "8080"));
        serverArray.add(new Server("Lee", "1.234.7.8", "9001"));
        serverArray.add(new Server("Jon", "45.78.12.34", "5000"));
        serverArray.add(new Server("Broom", "123.4.21.2", "4040"));
        serverArray.add(new Server("Lee", "76.12.56.43", "2003"));

        serverAdapter = new ServerCustomAdapter(MainActivity.this, R.layout.row, serverArray);
        serverList = (ListView) findViewById(R.id.listView);
        serverList.setItemsCanFocus(false);
        serverList.setAdapter(serverAdapter);

        serverList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                Log.i("List View Clicked", "**********");
                Toast.makeText(MainActivity.this,
                        "List View Clicked:" + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    protected void createDatabase(){
        db=openOrCreateDatabase("ServerConfigDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS servers(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ip VARCHAR,port VARCHAR, name varchar);");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showAddServers(){
        Intent intent = new Intent(this,AddServer.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == addNew){
            showAddServers();
        }
    }
}