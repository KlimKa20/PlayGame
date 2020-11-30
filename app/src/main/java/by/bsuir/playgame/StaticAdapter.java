package by.bsuir.playgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StaticAdapter extends ArrayAdapter<Statistics> {

    private LayoutInflater inflater;
    private int layout;
    private List<Statistics> statistics;
    private FirebaseDatabase database;


    public StaticAdapter(Context context, int resource, List<Statistics> statistics) {
        super(context, resource, statistics);
        this.statistics = statistics;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View view=inflater.inflate(this.layout, parent, false);

        TextView p1Name = (TextView) view.findViewById(R.id.p1Name);
        TextView p1Count = (TextView) view.findViewById(R.id.p1Count);

        TextView p2Name = (TextView) view.findViewById(R.id.p2Name);
        TextView p2Count = (TextView) view.findViewById(R.id.p2Count);

        TextView nameRoom = (TextView) view.findViewById(R.id.nameRoom);
        TextView status = (TextView) view.findViewById(R.id.status);


        Statistics statistic = statistics.get(position);

        p1Name.setText("Игрок 1: " + statistic.getP1Name());
        p1Count.setText("Кораблей: "+ statistic.getP1Ship());
        p2Name.setText(statistic.getP2Name() + " :Игрок 2");
        if(statistic.isStatus()){
            status.setText("Победа");
            status.setBackgroundColor(Color.GREEN);
        }
        else {
            status.setText("Поражение");
            status.setBackgroundColor(Color.RED);
        }
        p2Count.setText(statistic.getP2Ship() + " :Кораблей");
        nameRoom.setText("Комната: " + statistic.getNameRoom());

        return view;
    }


}
