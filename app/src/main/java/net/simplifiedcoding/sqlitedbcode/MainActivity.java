package net.simplifiedcoding.sqlitedbcode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    private static final String SELECT_SQL = "SELECT * FROM servers";

    ArrayList<Server> serverArray = new ArrayList<Server>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDatabase();

        addNew = (Button) findViewById(R.id.addNew);
        addNew.setOnClickListener(this);

        getResults();

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
                        "List View Clicked:" + position, Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    protected void createDatabase(){
        db=openOrCreateDatabase("ServerConfigDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS servers(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name varchar, ip VARCHAR,port VARCHAR, status BOOLEAN NOT NULL CHECK (status IN (0,1)));");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void getResults() {
        Cursor cursor = db.rawQuery(SELECT_SQL, null);
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String ip = cursor.getString(cursor.getColumnIndex("ip"));
                String port = cursor.getString(cursor.getColumnIndex("port"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));
                serverArray.add(new Server(name, ip, port, status));
            }
        }

        cursor.close();
        db.close();
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