package by.bsuir.playgame.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import by.bsuir.playgame.R;
import by.bsuir.playgame.Statistics;

public class StaticAdapter extends ArrayAdapter<Statistics> {

    private final LayoutInflater inflater;
    private final int layout;
    private final List<Statistics> statistics;

    public StaticAdapter(Context context, int resource, List<Statistics> statistics) {
        super(context, resource, statistics);
        this.statistics = statistics;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder") View view = inflater.inflate(this.layout, parent, false);

        TextView p1Name = (TextView) view.findViewById(R.id.p1Name);
        TextView p1Count = (TextView) view.findViewById(R.id.p1Count);

        TextView p2Name = (TextView) view.findViewById(R.id.p2Name);
        TextView p2Count = (TextView) view.findViewById(R.id.p2Count);

        ImageView p1View = (ImageView) view.findViewById(R.id.pView1);
        ImageView p2View = (ImageView) view.findViewById(R.id.pView2);

        TextView nameRoom = (TextView) view.findViewById(R.id.nameRoom);
        TextView status = (TextView) view.findViewById(R.id.status);

        Statistics statistic = statistics.get(position);

        p1Name.setText("Игрок 1: " + statistic.getP1Name());
        p1Count.setText("Кораблей: " + statistic.getP1Ship());
        p2Name.setText(statistic.getP2Name() + " :Игрок 2");
        if (statistic.isStatus()) {
            status.setText("Победа");
            view.setBackgroundColor(Color.GREEN);
        } else {
            status.setText("Поражение");
            view.setBackgroundColor(Color.RED);
        }
        Picasso.with(getContext())
                .load(statistic.getP1View())
                .into(p1View);
        Picasso.with(getContext())
                .load(statistic.getP2View())
                .into(p2View);
        p2Count.setText(statistic.getP2Ship() + " :Кораблей");
        nameRoom.setText("Комната: " + statistic.getNameRoom());

        return view;
    }


}
