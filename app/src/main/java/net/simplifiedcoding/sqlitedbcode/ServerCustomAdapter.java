package net.simplifiedcoding.sqlitedbcode;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ServerCustomAdapter extends ArrayAdapter<Server> {
    Context context;
    int layoutResourceId;

    private SQLiteDatabase db;

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
            holder.editButton = (Button) row.findViewById(R.id.editButton);
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
        holder.powerSwitch.setChecked(switchStatus);
        holder.editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Edit button Clicked",
                        Toast.LENGTH_SHORT).show();
            }
        });
        holder.powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openDatabase();
                if(isChecked) {
                    String sql = "UPDATE servers SET status=" + 1 + " WHERE ip='" +
                                server.getIp() + "' AND port='" + server.getPort() +"';";
                    db.execSQL(sql);
                    Toast.makeText(context,
                            "Power is ON",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    String sql = "UPDATE servers SET status=" + 0 + " WHERE ip='" +
                            server.getIp() + "' AND port='" + server.getPort() +"';";
                    db.execSQL(sql);
                    Toast.makeText(context,
                            "Power is OFF",
                            Toast.LENGTH_SHORT).show();
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
        Button editButton;
        Switch powerSwitch;
    }
}
