package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.ButtleViewModel;
import by.bsuir.playgame.ViewModel.DisplayViewModel;

public class RoomActivity extends AppCompatActivity {


    View fieldFragment1, fieldFragment2;
    TextView textView;
    String roomName;
    String role = "";
    ButtleViewModel buttleViewModel;
    DisplayViewModel displayViewModel;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messageRef, myRef, displayRef, buttleRef, restRef, staticticRef;

    int RestShip;
    boolean available = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent postIntent = getIntent();
        roomName = postIntent.getStringExtra("roomName");

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        buttleViewModel = ViewModelProviders.of(this).get(ButtleViewModel.class);
        displayViewModel = ViewModelProviders.of(this).get(DisplayViewModel.class);
        progressDialog = new ProgressDialog(this);

        textView = findViewById(R.id.namePlayer);
        fieldFragment1 = findViewById(R.id.fragment1);
        fieldFragment2 = findViewById(R.id.fragment2);

        myRef = database.getReference("rooms/" + roomName).child("p1").child("user");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
                    role = "host";
                    displayRef = database.getReference("rooms/" + roomName).child("p1").child("Field");
                    buttleRef = database.getReference("rooms/" + roomName).child("p2").child("Field");
                    restRef = database.getReference("rooms/" + roomName).child("p1").child("ship");
                    staticticRef = database.getReference("statistic/" + roomName).child("p1");
                } else {
                    displayRef = database.getReference("rooms/" + roomName).child("p2").child("Field");
                    buttleRef = database.getReference("rooms/" + roomName).child("p1").child("Field");
                    restRef = database.getReference("rooms/" + roomName).child("p2").child("ship");
                    staticticRef = database.getReference("statistic/" + roomName).child("p2");
                    role = "guest";
                }
                messageRef = database.getReference("rooms/" + roomName + "/message");
                messageRef.setValue(role);
                readDisplayEventListener();
                addButtleEventListener();
                addDisplayEventListener();
                addRoomEventListener();
                addListenerOfFinish();
                addWriterNameOfRoom();
                textView.setText("Ход противника");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        buttleViewModel.getShutElement().observe(this, s -> {
            if (available) {
                buttleRef.child(String.valueOf(Integer.parseInt(s))).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Objects.requireNonNull(snapshot.getValue()).toString().equals("0")) {
                            buttleRef.child(String.valueOf(Integer.parseInt(s))).setValue(3);
                            messageRef.setValue(role);
                            available = false;
                            textView.setText("Ход противника");
                        } else if (snapshot.getValue().toString().equals("1")) {
                            buttleRef.child(String.valueOf(Integer.parseInt(s))).setValue(2);
                        } else {
                            Toast.makeText(RoomActivity.this, "Выберите другую клеточку", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        displayViewModel.getDestoy().observe(this, stringObjectMap -> {
            displayRef.updateChildren(stringObjectMap);
            restRef.setValue(--RestShip);
        });
    }

    private void addWriterNameOfRoom() {
        database.getReference("rooms/" + roomName + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database.getReference("statistic/" + roomName + "/name").setValue(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addListenerOfFinish() {
        restRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RestShip = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()) == 0) {
                    Toast.makeText(RoomActivity.this, "Вы проиграли", Toast.LENGTH_LONG).show();
                    messageRef.setValue("Finish" + role);
                    available = false;
                    textView.setText("Вы проиграли");
                    createStatistic(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readDisplayEventListener() {
        displayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int[] temp = new int[100];
                for (DataSnapshot child : snapshot.getChildren()) {
                    temp[Integer.parseInt(Objects.requireNonNull(child.getKey()))] = Integer.parseInt(Objects.requireNonNull(child.getValue()).toString());
                }
                displayViewModel.setIcon(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addDisplayEventListener() {
        displayRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                displayViewModel.setIconId(Integer.parseInt(Objects.requireNonNull(snapshot.getKey())), Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()));

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addButtleEventListener() {
        buttleRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                buttleViewModel.setIconId(Integer.parseInt(Objects.requireNonNull(snapshot.getKey())), Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString()));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (role.equals("host")) {
                    if (Objects.equals(snapshot.getValue(String.class), "guest")) {
                        available = true;
                        textView.setText("Ваш Ход");
                    } else if (Objects.equals(snapshot.getValue(String.class), "Finishguest")) {
                        Toast.makeText(RoomActivity.this, "Победа", Toast.LENGTH_LONG).show();
                        available = false;
                        textView.setText("Вы выиграли");
                        createStatistic(RestShip);
                    }
                } else {
                    if (Objects.equals(snapshot.getValue(String.class), "host")) {
                        available = true;
                        textView.setText("Ваш Ход");
                    } else if (Objects.equals(snapshot.getValue(String.class), "Finishhost")) {
                        Toast.makeText(RoomActivity.this, "Победа", Toast.LENGTH_LONG).show();
                        available = false;
                        textView.setText("Вы выиграли");
                        createStatistic(RestShip);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createStatistic(int count) {
        staticticRef.child("name").setValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        staticticRef.child("ship").setValue(count);
        setResult(RESULT_OK);
        finish();

    }
}