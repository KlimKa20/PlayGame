package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.ButtleViewModel;
import by.bsuir.playgame.ViewModel.DisplayViewModel;

public class RoomActivity extends AppCompatActivity {


    View fieldFragment1, fieldFragment2;
    String roomName;
    String role = "";
    String message = "";
    ButtleViewModel buttleViewModel;
    DisplayViewModel displayViewModel;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messageRef, myRef, displayRef, buttleRef;

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
//                    progressDialog.setMessage("Please wait second player\n ID room: " + roomName);
//                    progressDialog.show();
//                    progressDialog.setCanceledOnTouchOutside(false);
                } else {
                    displayRef = database.getReference("rooms/" + roomName).child("p2").child("Field");
                    buttleRef = database.getReference("rooms/" + roomName).child("p1").child("Field");
                    role = "guest";
                }
                messageRef = database.getReference("rooms/" + roomName + "/message");
                message = role;
                messageRef.setValue(message);
                readDisplayEventListener();
                addButtleEventListener();
                addDisplayEventListener();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        buttleViewModel.getShutElement().observe(this, s -> {
            buttleRef.child(String.valueOf(Integer.parseInt(s))).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue().toString().equals("0")) {
                        buttleRef.child(String.valueOf(Integer.parseInt(s))).setValue(3);
                    } else {
                        buttleRef.child(String.valueOf(Integer.parseInt(s))).setValue(2);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        displayViewModel.getDestoy().observe(this, stringObjectMap -> displayRef.updateChildren(stringObjectMap));
    }

    //buttleRef.child(String.valueOf(Integer.parseInt(s))).addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            if (snapshot.getValue().toString().equals("0")) {
//                buttleRef.child(String.valueOf(Integer.parseInt(s))).setValue(3);
//            } else {
//                int position = Integer.parseInt(s);
//                buttleRef.child(String.valueOf(position)).setValue(2);
//                ArrayList<Integer> temp = new ArrayList<Integer>();
//                if (position % 10 == 0) {
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position + i));
//                        if (result == 2) {
//                            temp.add(position + i);
//                        } else if (result == 0) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                } else if (position % 10 == 9) {
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position - i));
//                        if (result == 2) {
//                            temp.add(position - i);
//                        } else if (result == 0) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                } else {
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position + i));
//                        if (result == 2) {
//                            temp.add(position + i);
//                        } else if (result == 0) {
//                            break;
//                        } else if (position + 1 % 10 == 9) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                    for (int i = 1; i < 4 - temp.size(); i++) {
//                        int result = check(String.valueOf(position - i));
//                        if (result == 2) {
//                            temp.add(position - i);
//                        } else if (result == 0) {
//                            break;
//                        } else if (position - 1 % 10 == 0) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                }
//                if (temp.size() != 0) {
//                    destroyShip(temp, true);
//                } else if (position < 10) {
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position + i * 10));
//                        if (result == 2) {
//                            temp.add(position + i);
//                        } else if (result == 0) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                } else if (position > 89) {
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position - i));
//                        if (result == 2) {
//                            temp.add(position - i * 10);
//                        } else if (result == 0) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                } else {
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position + i * 10));
//                        if (result == 2) {
//                            temp.add(position + i);
//                        } else if (result == 0) {
//                            break;
//                        } else if (position + i > 89) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                    for (int i = 1; i < 4; i++) {
//                        int result = check(String.valueOf(position - i));
//                        if (result == 2) {
//                            temp.add(position - i * 10);
//                        } else if (result == 0) {
//                            break;
//                        } else if (position - i < 10) {
//                            break;
//                        } else {
//                            return;
//                        }
//                    }
//                }
//                destroyShip(temp, false);
//
//            }
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    });
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
                messageRef.setValue(message);
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

//    public int check(String position) {
//        final int[] temp = {0};
//        buttleRef.child(position).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getValue().toString().equals("2")) {
//                    temp[0] = 2;
//                } else if (snapshot.getValue().toString().equals("1")) {
//                    temp[0] = 1;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        return temp[0];
//    }

//    public void destroyShip(ArrayList<Integer> ship, boolean horizonatal) {
//        Map<String, Object> temp = new HashMap<>();
//        if (horizonatal) {
//            for (int element : ship) {
//                if (element > 9) {
//                    temp.put(String.valueOf(element - 10), "3");
//                }
//                if (element < 90) {
//                    temp.put(String.valueOf(element + 10), "3");
//                }
//            }
//            int edge = ship.get(0);
//            if (edge % 10 != 0) {
//                temp.put(String.valueOf(edge - 1), "3");
//                if (edge > 9) {
//                    temp.put(String.valueOf(edge - 11), "3");
//                }
//                if (edge < 90) {
//                    temp.put(String.valueOf(edge + 9), "3");
//                }
//            }
//            edge = ship.get(ship.size() - 1);
//            if (edge % 10 != 9) {
//                temp.put(String.valueOf(edge + 1), "3");
//                if (edge > 9) {
//                    temp.put(String.valueOf(edge - 9), "3");
//                }
//                if (edge < 90) {
//                    temp.put(String.valueOf(edge + 11), "3");
//                }
//            }
//        } else {
//            for (int element : ship) {
//                if (element % 10 != 0) {
//                    temp.put(String.valueOf(element - 1), "3");
//                }
//                if (element % 10 != 9) {
//                    temp.put(String.valueOf(element + 11), "3");
//                }
//            }
//            int edge = ship.get(0);
//            if (edge > 9) {
//                temp.put(String.valueOf(edge - 10), "3");
//                if (edge % 10 != 0) {
//                    temp.put(String.valueOf(edge - 11), "3");
//                }
//                if (edge % 10 != 9) {
//                    temp.put(String.valueOf(edge - 9), "3");
//                }
//            }
//            edge = ship.get(ship.size() - 1);
//            if (edge < 90) {
//                temp.put(String.valueOf(edge + 10), "3");
//                if (edge % 10 != 0) {
//                    temp.put(String.valueOf(edge + 9), "3");
//                }
//                if (edge % 10 != 9) {
//                    temp.put(String.valueOf(edge + 11), "3");
//                }
//            }
//        }
//        buttleRef.updateChildren(temp);
//
//    }

}