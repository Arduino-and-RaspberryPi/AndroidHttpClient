package net.simplifiedcoding.sqlitedbcode;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
        } else {
            holder = (ServerHolder) row.getTag();
        }
        Server user = data.get(position);
        holder.textName.setText(user.getName());
        holder.textIP.setText(user.getIp());
        holder.textPort.setText(user.getPort());
        holder.powerSwitch.setChecked(false);
        holder.editButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("Edit Button Clicked", "**********");
                Toast.makeText(context, "Edit button Clicked",
                        Toast.LENGTH_LONG).show();
            }
        });
        holder.powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(context,
                            "Switch is currently ON",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context,
                            "Switch is currently OFF",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        if(holder.powerSwitch.isChecked()){
            Toast.makeText(context,
                    "Switch is currently ON",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context,
                    "Switch is currently OFF",
                    Toast.LENGTH_LONG).show();
        }

            return row;

    }

    static class ServerHolder {
        TextView textName;
        TextView textIP;
        TextView textPort;
        Button editButton;
        Switch powerSwitch;
    }
}
