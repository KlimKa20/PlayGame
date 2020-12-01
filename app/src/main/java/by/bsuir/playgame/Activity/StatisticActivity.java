package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import by.bsuir.playgame.Adapter.StaticAdapter;
import by.bsuir.playgame.R;
import by.bsuir.playgame.Statistics;


public class StatisticActivity extends AppCompatActivity {

    private final List<Statistics> statistics = new ArrayList();
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private DatabaseReference myRef;

    private String p1Name, p2Name, p1Count, p2Count, point, currentName, nameRoom;
    HashMap<String, List<String>> names = new HashMap<>();

    ListView statisticList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        database = FirebaseDatabase.getInstance();
        currentName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        myRef = database.getReference("Users");
        statisticList = (ListView) findViewById(R.id.statisticList);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        addReadAllName();
    }

    public void addReadAllName() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    List<String> temp = new ArrayList<>();
                    temp.add( Objects.requireNonNull(child.child("userName").getValue()).toString());
                    temp.add( Objects.requireNonNull(child.child("Image").getValue()).toString());
                    names.put(child.getKey(),temp);
                }
                myRef = database.getReference("statistic");
                addListenerStatistic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void addListenerStatistic() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    for (DataSnapshot child1 : child.getChildren()) {
                        point = child1.getKey();
                        if (!Objects.requireNonNull(point).equals("name")) {
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
                            nameRoom = Objects.requireNonNull(child1.getValue()).toString();
                        }
                    }
                    if (p1Name.equals(currentName)) {
                        if (p1Count.equals("0")) {
                            statistics.add(new Statistics(names.get(p1Name).get(0), names.get(p2Name).get(0),names.get(p1Name).get(1), names.get(p2Name).get(1), nameRoom, false, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        } else {
                            statistics.add(new Statistics(names.get(p1Name).get(0), names.get(p2Name).get(0),names.get(p1Name).get(1), names.get(p2Name).get(1), nameRoom, true, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        }
                    } else if (p2Name.equals(currentName)) {
                        if (p2Count.equals("0")) {
                            statistics.add(new Statistics(names.get(p1Name).get(0), names.get(p2Name).get(0),names.get(p1Name).get(1), names.get(p2Name).get(1), nameRoom, false, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        } else {
                            statistics.add(new Statistics(names.get(p1Name).get(0), names.get(p2Name).get(0),names.get(p1Name).get(1), names.get(p2Name).get(1), nameRoom, true, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                        }
                    }
                }
                StaticAdapter stateAdapter = new StaticAdapter(getApplicationContext(), R.layout.statistic_item, statistics);
                statisticList.setAdapter(stateAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}