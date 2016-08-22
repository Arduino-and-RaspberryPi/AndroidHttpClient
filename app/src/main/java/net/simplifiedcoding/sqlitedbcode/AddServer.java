package net.simplifiedcoding.sqlitedbcode;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddServer extends ActionBarActivity implements View.OnClickListener {
    private EditText editTextName;
    private EditText editTextAdd;
    private Button btnAdd;
    private Button btnView;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);

//        createDatabase();

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAdd = (EditText) findViewById(R.id.editTextAddress);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnView = (Button) findViewById(R.id.btnView);

        btnAdd.setOnClickListener(this);
        btnView.setOnClickListener(this);
    }


    protected void createDatabase(){
        db=openOrCreateDatabase("ServerConfigDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS servers(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ip VARCHAR,port VARCHAR);");
    }

    protected void insertIntoDB(){
        String name = editTextName.getText().toString().trim();
        String add = editTextAdd.getText().toString().trim();
        if(name.equals("") || add.equals("")){
            Toast.makeText(getApplicationContext(),"Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        String query = "INSERT INTO servers (ip,port" +
                ") VALUES('"+name+"', '"+add+"');";
        db.execSQL(query);
        Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG).show();
    }

    private void showServers(){
        Intent intent = new Intent(this,ViewServers.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        if(v == btnAdd){
            insertIntoDB();
        }
        if (v == btnView) {
            showServers();
        }
    }
}
