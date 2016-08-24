package net.simplifiedcoding.sqlitedbcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ServerCustomAdapter extends ArrayAdapter<Server> {
    Context context;
    int layoutResourceId;

    private SQLiteDatabase db;
    private String httpResponse = null;

    ArrayList<Server> data = new ArrayList<Server>();

    public ServerCustomAdapter(Context context, int layoutResourceId,
                               ArrayList<Server> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ServerHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ServerHolder();
            holder.textName = (TextView) row.findViewById(R.id.textViewName);
            holder.textIP = (TextView) row.findViewById(R.id.textViewIP);
            holder.textPort = (TextView) row.findViewById(R.id.textViewPort);
            holder.powerSwitch = (Switch) row.findViewById(R.id.powerSwitch);
            row.setTag(holder);
        }
        else {
            holder = (ServerHolder) row.getTag();
        }
        final Server server = data.get(position);
        holder.textName.setText(server.getName());
        holder.textIP.setText(server.getIp());
        holder.textPort.setText(server.getPort());
        final Boolean switchStatus = server.getStatus() == 1 ? true : false;
        holder.powerSwitch.setChecked(switchStatus);
        holder.powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openDatabase();
                if (isChecked) {
                    String URL = "http://" + server.getIp() + ":" + server.getPort() + "/on$";
                    HttpGetRequest task = new HttpGetRequest();
                    task.execute(new String[]{URL});
                    if(httpResponse != null && !httpResponse.isEmpty()) {
                        String sql = "UPDATE servers SET status=" + 1 + " WHERE ip='" +
                                server.getIp() + "' AND port='" + server.getPort() + "';";
                        db.execSQL(sql);
                        showMessage("Power is ON at " + URL);
                    }
                    else{
                        showMessage("Server is not responding or you are offline.");
                    }
                }
                else {
                    String URL = "http://" + server.getIp() + ":" + server.getPort() + "/off$";
                    HttpGetRequest task = new HttpGetRequest();
                    task.execute(new String[]{URL});
                    if(httpResponse != null && !httpResponse.isEmpty()) {
                        String sql = "UPDATE servers SET status=" + 0 + " WHERE ip='" +
                                server.getIp() + "' AND port='" + server.getPort() + "';";
                        db.execSQL(sql);
                        showMessage("Power is OFF at " + URL);
                    }
                    else{
                        showMessage("Server is not responding or you are offline.");
                    }
                }
                db.close();
            }
        });
        return row;

    }

    protected void openDatabase() {
        db = SQLiteDatabase.openDatabase(context.getDatabasePath("ServerConfigDB").toString(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    static class ServerHolder {
        TextView textName;
        TextView textIP;
        TextView textPort;
        Switch powerSwitch;
    }

    private void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private class HttpGetRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                for (String url : urls) {
                    httpResponse = getOutputFromUrl(url);
                }
                return httpResponse;
            }
            catch (Exception ex){
                ex.printStackTrace();
                showMessage("Server is not responding");
            }
            return null;
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
            } catch (IOException ex) {
                ex.printStackTrace();
                showMessage("Server is not responding");
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
                showMessage("Server is not responding");
            }
            return stream;
        }
    }
}
