package net.simplifiedcoding.sqlitedbcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ViewServers extends ActionBarActivity implements View.OnClickListener{
    private EditText editTextIP;
    private EditText editTextName;
    private EditText editTextPort;
    private EditText editTextStatus;
    private EditText editTextId;
    private Button btnPrev;
    private Button btnNext;
    private Button btnSave;
    private Button btnDelete;

    private static final String SELECT_SQL = "SELECT * FROM servers";

    private SQLiteDatabase db;

    private Cursor cursor;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_servers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("SERVER", position);
        }

        try {
            openDatabase();
            cursor = db.rawQuery(SELECT_SQL, null);
            cursor.moveToPosition(position);
            showRecords();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No records found.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initializeViews() {
        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextIP = (EditText) findViewById(R.id.editTextIP);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        editTextStatus = (EditText) findViewById(R.id.editTextStatus);

        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    protected void openDatabase() {
        db = openOrCreateDatabase("ServerConfigDB", Context.MODE_PRIVATE, null);
    }

    protected void showRecords() {
        String id = cursor.getString(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String ip = cursor.getString(cursor.getColumnIndex("ip"));
        String port = cursor.getString(cursor.getColumnIndex("port"));
        int status = cursor.getInt(cursor.getColumnIndex("status"));
        editTextId.setText(id);
        editTextName.setText(name);
        editTextIP.setText(ip);
        editTextPort.setText(port);
        String switchStatus =  (status == 1) ? "ON" : "OFF";
        editTextStatus.setText(switchStatus);
    }

    protected void moveNext() {
        try {
            if (!cursor.isLast())
                cursor.moveToNext();

            showRecords();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No next records found.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    protected void movePrev() {
        try {
            if (!cursor.isFirst())
                cursor.moveToPrevious();

            showRecords();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No previous records found.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    protected void saveRecord() {
        String id = editTextId.getText().toString().trim();
        String ip = editTextIP.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String port = editTextPort.getText().toString().trim();
        String status = editTextStatus.getText().toString().trim();

        String sql = "UPDATE servers SET name='" + name + "',ip='" + ip + "', port='" + port + "', status='" + status + "' WHERE id=" + id + ";";

        if (ip.equals("") || port.equals("")) {
            Toast.makeText(getApplicationContext(), "You cannot save blank values", Toast.LENGTH_SHORT).show();
            return;
        }

        db.execSQL(sql);
        Toast.makeText(getApplicationContext(), "Records Saved Successfully", Toast.LENGTH_SHORT).show();
        cursor = db.rawQuery(SELECT_SQL, null);
        cursor.moveToPosition(Integer.parseInt(id));
    }

    private void deleteRecord() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want delete this server configuration?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String id = editTextId.getText().toString().trim();

                        String sql = "DELETE FROM servers WHERE id=" + id + ";";
                        db.execSQL(sql);
                        Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_SHORT).show();
                        cursor = db.rawQuery(SELECT_SQL, null);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
            moveNext();
        }

        if (view == btnPrev) {
            movePrev();
        }

        if (view == btnSave) {
            saveRecord();
        }

        if (view == btnDelete) {
            deleteRecord();
        }
    }

}