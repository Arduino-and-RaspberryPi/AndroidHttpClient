package net.simplifiedcoding.sqlitedbcode;

import java.io.IOException;
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

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class ServerCustomAdapter extends ArrayAdapter<Server> {
    Context context;
    int layoutResourceId;

    private SQLiteDatabase db;
    private String httpResponse = null;
    private OkHttpClient client = new OkHttpClient();

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
                if (isChecked) {
                    loadContent("http", ip, port, "on");
                    if(httpResponse != null && !httpResponse.isEmpty()) {
                        String sql = "UPDATE servers SET status=" + 1 + " WHERE ip='" +
                                server.getIp() + "' AND port='" + server.getPort() + "';";
                        db.execSQL(sql);
                        showMessage("Power is ON at " + ip+":"+port);
                    }
                    else{
                        showMessage("Server is not responding or you are offline.");
                    }
                }
                else {
                    loadContent("http", ip, port, "off");
                    if(httpResponse != null && !httpResponse.isEmpty()) {
                        String sql = "UPDATE servers SET status=" + 0 + " WHERE ip='" +
                                server.getIp() + "' AND port='" + server.getPort() + "';";
                        db.execSQL(sql);
                        showMessage("Power is OFF at " +  ip+":"+port);
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

    private String getSwitchStatus(String ip, int port){
        loadContent("http", ip, port, "status");
        try { Thread.sleep(3000); }
        catch (InterruptedException e) { e.printStackTrace(); }
        if(httpResponse != null && !httpResponse.isEmpty()) {
            int startPosition = httpResponse.indexOf("<html>") + "<html>".length();
            int endPosition = httpResponse.indexOf("</html>", startPosition);
            String status = httpResponse.substring(startPosition, endPosition);
            return status.substring(status.length() -1);
        }
        return "";
    }

    private void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void loadContent(final String requestType, final String ip, final int port, final String path) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpUrl httpUrl = RequestBuilder.buildURL(requestType, ip, port, path);
                    httpResponse = ApiCall.GET(client, httpUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
