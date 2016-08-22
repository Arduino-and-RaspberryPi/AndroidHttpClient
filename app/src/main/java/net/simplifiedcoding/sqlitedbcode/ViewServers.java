package net.simplifiedcoding.sqlitedbcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ViewServers extends ActionBarActivity implements View.OnClickListener{
    private EditText editTextIP;
    private EditText editTextPort;
    private EditText editTextId;
    private Button btnPrev;
    private Button btnNext;
    private Button btnSave;
    private Button btnDelete;
    private Switch powerSwitch;

    private static final String SELECT_SQL = "SELECT * FROM servers";

    private SQLiteDatabase db;

    private Cursor c;
    private Button button;//
    private TextView outputText;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_servers);
        openDatabase();

        findViewsById();//
        button.setOnClickListener(this);//

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextIP = (EditText) findViewById(R.id.editTextIP);
        editTextPort = (EditText) findViewById(R.id.editTextPort);

        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        powerSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    outputText.setText("Switch is currently ON");  //To change the text near to switch
                }
                else {
                    outputText.setText("Switch is currently OFF");  //To change the text near to switch
                }
            }
        });
        if(powerSwitch.isChecked()){
            outputText.setText("Switch is currently ON");
        }
        else {
            outputText.setText("Switch is currently OFF");
        }

        try {
            c = db.rawQuery(SELECT_SQL, null);
            c.moveToFirst();
            showRecords();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No records found.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void findViewsById() {//
        button = (Button) findViewById(R.id.button);
        outputText = (TextView) findViewById(R.id.outputTxt);
        powerSwitch = (Switch) findViewById(R.id.powerSwitch);
        powerSwitch.setChecked(false);
    }//

    protected void openDatabase() {
        db = openOrCreateDatabase("ServerConfigDB", Context.MODE_PRIVATE, null);
    }

    protected void showRecords() {
        String id = c.getString(0);
        String ip = c.getString(1);
        String port = c.getString(2);
        editTextId.setText(id);
        editTextIP.setText(ip);
        editTextPort.setText(port);
    }

    protected void moveNext() {
        try {
            if (!c.isLast())
                c.moveToNext();

            showRecords();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No next records found.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    protected void movePrev() {
        try {
            if (!c.isFirst())
                c.moveToPrevious();

            showRecords();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "No previous records found.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }


    protected void saveRecord() {
        String ip = editTextIP.getText().toString().trim();
        String port = editTextPort.getText().toString().trim();
        String id = editTextId.getText().toString().trim();

        String sql = "UPDATE servers SET ip='" + ip + "', port='" + port + "' WHERE id=" + id + ";";

        if (ip.equals("") || port.equals("")) {
            Toast.makeText(getApplicationContext(), "You cannot save blank values", Toast.LENGTH_LONG).show();
            return;
        }

        db.execSQL(sql);
        Toast.makeText(getApplicationContext(), "Records Saved Successfully", Toast.LENGTH_LONG).show();
        c = db.rawQuery(SELECT_SQL, null);
        c.moveToPosition(Integer.parseInt(id));
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
                        Toast.makeText(getApplicationContext(), "Record Deleted", Toast.LENGTH_LONG).show();
                        c = db.rawQuery(SELECT_SQL, null);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_servers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            moveNext();
        }

        if (v == btnPrev) {
            movePrev();
        }

        if (v == btnSave) {
            saveRecord();
        }

        if (v == btnDelete) {
            deleteRecord();
        }
        if (v == button) {
            String ip = editTextIP.getText().toString().trim();
            String port = editTextPort.getText().toString().trim();
            String URL = "http://"+ip+":"+port+"/on$";
            HttpGetRequest task = new HttpGetRequest();
            task.execute(new String[] { URL });
        }
    }

    private class HttpGetRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }

        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString) throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String output) {
            outputText.setText(output);
        }
    }
}