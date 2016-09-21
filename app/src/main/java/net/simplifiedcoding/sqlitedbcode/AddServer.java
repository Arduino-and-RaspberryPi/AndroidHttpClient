package net.simplifiedcoding.sqlitedbcode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddServer extends ActionBarActivity implements View.OnClickListener {
    private EditText editTextName;
    private EditText editTextIP;
    private EditText editTextPort;
    private EditText editTextCommand;
    private Button btnAdd;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initializeViews();
    }

    private void initializeViews(){
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextIP = (EditText) findViewById(R.id.editTextIP);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        editTextCommand = (EditText) findViewById(R.id.editTextCommand);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(this);
    }


    protected void openDatabase(){
        db=openOrCreateDatabase("ESPServerDB", Context.MODE_PRIVATE, null);
    }

    protected void insertIntoDB(){
        openDatabase();
        String name = editTextName.getText().toString().trim();
        String ip = editTextIP.getText().toString().trim();
        String command = editTextCommand.getText().toString().trim();
        String status = "0";
        String strPort = editTextPort.getText().toString().trim();
        int port = strPort.isEmpty() ? 0 : Integer.parseInt(strPort);
        if(name.equals("") || ip.equals("")){
            showMessage("Please fill all mandatory fields.");
            return;
        }

        String query = "INSERT INTO servers (name, ip, port, command, status" +
                ") VALUES('"+name+"', '"+ip+"', '"+port+"','"+command+"', '"+status+"');";
        try {
            db.execSQL(query);
            showMessage("Saved Successfully.");
        }
        catch (Exception ex){
            showMessage("Record with this name already exist.");
        }
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if(view == btnAdd){
            insertIntoDB();
        }
    }

    private void showMessage(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background_color);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
