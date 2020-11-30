package by.bsuir.playgame.Activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import by.bsuir.playgame.R;
import by.bsuir.playgame.StaticAdapter;
import by.bsuir.playgame.Statistics;


public class StatisticActivity extends AppCompatActivity {

    private List<Statistics> statistics = new ArrayList();
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;

    private String p1Name, p2Name, p1Count, p2Count, point, currentName, nameRoom;

    ListView statisticList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("userName");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentName = Objects.requireNonNull(snapshot.getValue()).toString();
                myRef = database.getReference("statistic");
                addListenerStatistic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        statisticList = (ListView) findViewById(R.id.statisticList);
    }

    public void addListenerStatistic() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot child1 : child.getChildren()) {
                        point = child1.getKey();
                        if (!point.equals("name")) {
                            for (DataSnapshot child2 : child1.getChildren()) {
                                if (point.equals("p1")) {
                                    if (Objects.equals(child2.getKey(), "ship")) {
                                        p1Count = Objects.requireNonNull(child2.getValue()).toString();
                                    } else {
                                        p1Name = Objects.requireNonNull(child2.getValue()).toString();
                                    }
                                } else {
                                    if (Objects.equals(child2.getKey(), "ship")) {
                                        p2Count = Objects.requireNonNull(child2.getValue()).toString();
                                    } else {
                                        p2Name = Objects.requireNonNull(child2.getValue()).toString();
                                    }
                                }
                            }
                        } else {
                            nameRoom = child1.getValue().toString();
                        }
                    }
                    if (p1Name.equals(currentName)) {
                        if (p1Count.equals("0")) {
                            statistics.add(new Statistics(p1Name, p2Name, nameRoom, false, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        } else {
                            statistics.add(new Statistics(p1Name, p2Name, nameRoom, true, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        }
                    } else if (p2Name.equals(currentName)) {
                        if (p2Count.equals("0")) {
                            statistics.add(new Statistics(p1Name, p2Name, nameRoom, false, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        } else {
                            statistics.add(new Statistics(p1Name, p2Name, nameRoom, true, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        }                    }
                }
                StaticAdapter stateAdapter = new StaticAdapter(getApplicationContext(), R.layout.statistic_item, statistics);
                statisticList.setAdapter(stateAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}