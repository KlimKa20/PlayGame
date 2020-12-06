package by.bsuir.playgame.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import by.bsuir.playgame.Model.ModelFirebaseAuth;
import by.bsuir.playgame.Model.ModelFirebaseDatabase;
import by.bsuir.playgame.Model.Statistics;

public class StatisticViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Statistics>> listStatistics = new MutableLiveData<>();

    ModelFirebaseDatabase firebaseDatabase;
    ModelFirebaseAuth firebaseAuth;
    HashMap<String, List<String>> names;


    public StatisticViewModel(@NonNull Application application) {
        super(application);
        firebaseDatabase = new ModelFirebaseDatabase();
        firebaseAuth = new ModelFirebaseAuth();
        names = new HashMap<>();
    }

    public LiveData<List<Statistics>> getListStatistics() {
        return listStatistics;
    }

    public void readAllName() {
        firebaseDatabase.getRef("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    List<String> temp = new ArrayList<>();
                    temp.add(Objects.requireNonNull(child.child("userName").getValue()).toString());
                    temp.add(Objects.requireNonNull(child.child("Image").getValue()).toString());
                    names.put(child.getKey(), temp);
                }
                addListenerStatistic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addListenerStatistic() {
        firebaseDatabase.getRef("statistic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String p1Name = null, p2Name = null, p1Count = null, p2Count = null, point, currentName = firebaseAuth.getUIDUser(), nameRoom = null;
                List<Statistics> statistics = new ArrayList();
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
                        boolean status = !p1Count.equals("0");
                        statistics.add(new Statistics(names.get(p1Name).get(0), names.get(p2Name).get(0), names.get(p1Name).get(1), names.get(p2Name).get(1), nameRoom, status, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                    } else if (p2Name.equals(currentName)) {
                        boolean status = !p2Count.equals("0");
                        statistics.add(new Statistics(names.get(p1Name).get(0), names.get(p2Name).get(0), names.get(p1Name).get(1), names.get(p2Name).get(1), nameRoom, status, Integer.parseInt(p1Count), Integer.parseInt(p2Count)));
                    }
                }
                listStatistics.setValue(statistics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
