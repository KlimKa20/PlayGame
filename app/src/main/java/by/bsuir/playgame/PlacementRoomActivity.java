package by.bsuir.playgame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PlacementRoomActivity extends AppCompatActivity {

    ShipViewModel shipViewModel;
    Button button5, button4, button3, button2, button1, buttlebutton;
    TextView textView4, textView3, textView2, textView1;
    String roomName ;
    String role = "";
    String message = "";

    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference messageRef,myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent postIntent = getIntent();
        roomName =postIntent.getStringExtra("roomName");
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("rooms/" + roomName).child("p1").child("user");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())){
                    role = "host";
                    progressDialog.setMessage("Please wait second player\n ID room: "+roomName);
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                else {
                    role = "guest";
                }
                messageRef = database.getReference("rooms/" + roomName + "/message");
                message = role + ":Пуньк";
                messageRef.setValue(message);
                addRoomEventListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        shipViewModel = ViewModelProviders.of(this).get(ShipViewModel.class);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        buttlebutton = findViewById(R.id.buttle);
        textView1 = findViewById(R.id.textView6);
        textView2 = findViewById(R.id.textView5);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView2);
        button1.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) > 0)
                shipViewModel.setShip("1");
        });
        button2.setOnClickListener(item -> {
            if (Integer.parseInt(textView2.getText().toString()) > 0)
                shipViewModel.setShip("2");
        });
        button3.setOnClickListener(item -> {
            if (Integer.parseInt(textView3.getText().toString()) > 0)
                shipViewModel.setShip("3");
        });
        button4.setOnClickListener(item -> {
            if (Integer.parseInt(textView4.getText().toString()) > 0)
                shipViewModel.setShip("4");
        });

        buttlebutton.setOnClickListener(item-> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) == 0) {
                myRef  = database.getReference("rooms/").child(roomName).child("p1").child("user");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (Objects.requireNonNull(dataSnapshot.getValue()).toString().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() )) {
                            shipViewModel.fillDB("/rooms/" + roomName + "/p1/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/Field");
                        }
                        else
                        {
                            shipViewModel.fillDB("/rooms/" + roomName + "/p2/" + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/Field");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            } else {
                Toast.makeText(this, "Раставленны не все корабли", Toast.LENGTH_SHORT).show();
            }

        });

        button5.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) != 10) {
                shipViewModel.setShip("-1");
            } else {
                Toast.makeText(this, "На поле нет кораблей", Toast.LENGTH_SHORT).show();
            }
        });
        shipViewModel.getResultOfSetShip().observe(this, s -> {
            int sign = -1;
            if (s < 0) {
                sign = 1;
                s*=-1;
            }
            switch (s) {
                case 1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString()) + sign));
                    break;
                case 2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) + sign));
                    break;
                case 3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) + sign));
                    break;
                case 4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) + sign));
                    break;
            }
        });

    }
    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(role.equals("host")){
                    if(Objects.requireNonNull(snapshot.getValue(String.class)).contains("guest:")){
                        progressDialog.dismiss();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                messageRef.setValue(message);
            }
        });
    }
}