package net.simplifiedcoding.sqlitedbcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class ServerCustomAdapter extends ArrayAdapter<Server> {
    Context context;
    int layoutResourceId;

    private SQLiteDatabase db;
    private OkHttpClient client = new OkHttpClient()
                                 .newBuilder()
                                 .connectTimeout(500, TimeUnit.MILLISECONDS)
                                 .build();

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
        Boolean switchStatus = server.getStatus() == 1 ? true : false;
        String status = getSwitchStatus(server.getIp(), Integer.parseInt(server.getPort()));
        switchStatus = status.isEmpty() ? switchStatus : status.equals("1");
        holder.powerSwitch.setChecked(switchStatus);
        holder.powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openDatabase();
                String ip = server.getIp();
                int port = Integer.parseInt(server.getPort());
                if (isChecked)
                    updateSwitchStatus(ip, port, "on");
                else
                    updateSwitchStatus(ip, port, "off");
                db.close();
            }
        });
        return row;

    }

    protected void openDatabase() {
        db = SQLiteDatabase.openDatabase(context.getDatabasePath("ESPServerDB").toString(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    static class ServerHolder {
        TextView textName;
        TextView textIP;
        TextView textPort;
        Switch powerSwitch;
    }

    private void updateSwitchStatus(String ip, int port,String status){
        String httpResponse = loadContent("http", ip, port, status);
        if(httpResponse != null && !httpResponse.isEmpty()) {
            int newStatus = status.equals("on") ? 1 : 0;
            String sql = "UPDATE servers SET status=" + newStatus + " WHERE ip='" +
                    ip + "' AND port='" + port + "';";
            db.execSQL(sql);
            showMessage("Power is "+status.toUpperCase()+" at " + ip+":"+port);
        }
        else{
            showMessage("Server is not responding or you are offline.");
        }
    }

    private String getSwitchStatus(String ip, int port){
        String httpResponse = loadContent("http", ip, port, "status");
        String status = "";
        if(httpResponse != null && !httpResponse.isEmpty()) {
            status = getStatusFromResponse(httpResponse);
        }
        return status;
    }

    private String getStatusFromResponse(String httpResponse){
        int startPosition = httpResponse.indexOf("<html>") + "<html>".length();
        int endPosition = httpResponse.indexOf("</html>", startPosition);
        String status = httpResponse.substring(startPosition, endPosition);
        return status.substring(status.length() -1);
    }

    private void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private String loadContent(final String requestType, final String ip, final int port, final String path) {
        String response = "";
        try {
            response = new AsyncTask<String, Integer, String>() {

                @Override
                protected String doInBackground(String... params) {
                    String httpResponse = "";
                    try {
                        HttpUrl httpUrl = RequestBuilder.buildURL(requestType, ip, port, path);
                        httpResponse = ApiCall.GET(client, httpUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return httpResponse;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
