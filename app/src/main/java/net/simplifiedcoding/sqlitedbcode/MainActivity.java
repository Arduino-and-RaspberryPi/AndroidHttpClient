package net.simplifiedcoding.sqlitedbcode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private Menu optionsMenu;
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

        openOrCreateDatabase();

        addNew = (Button) findViewById(R.id.addNew);
        addNew.setOnClickListener(this);

        getResults();
        serverAdapter = new ServerCustomAdapter(MainActivity.this, R.layout.row, serverArray);
        serverList = (ListView) findViewById(R.id.listView);
        serverList.setItemsCanFocus(false);
        serverList.setAdapter(serverAdapter);

        if(serverAdapter.isEmpty())
            showMessage("No servers found. Click '+' button to add new server.");

        serverList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                viewServers(position);
            }
        });
    }

    protected void openOrCreateDatabase(){
        db=openOrCreateDatabase("ESPServerDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS servers(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name varchar, ip VARCHAR,port VARCHAR, command VARCHAR, status BOOLEAN NOT NULL CHECK (status IN (0,1)), CONSTRAINT name_unique UNIQUE (name));");
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
    }

    private void viewServers(int position){
        Intent intent = new Intent(this,EditServer.class);
        intent.putExtra("SERVER", position);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view == addNew){
            showAddServers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuRefresh:
                finish();
                startActivity(getIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background_color);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}