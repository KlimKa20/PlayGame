package by.bsuir.playgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomActivity extends AppCompatActivity {


    Button button;

    String playerName = "";
    String roomName ;
    String role = "";
    String message = "";
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference messageRef,myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);


        Intent postIntent = getIntent();
        playerName =postIntent.getStringExtra("playerName");
        roomName =postIntent.getStringExtra("roomName");

        button = findViewById(R.id.click);
        button.setEnabled(false);
        progressDialog = new ProgressDialog(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("rooms/" + roomName).child("p1");


        database.getReference("rooms/" + roomName).child("name").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getSupportActionBar().hide();
//                        getSupportActionBar().setTitle(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals(playerName)){
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


        button.setOnClickListener(v -> {
            button.setEnabled(false);
            message = role + ":Пуньк";
            messageRef.setValue(message);
        });

    }

    private void addRoomEventListener() {
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(role.equals("host")){
                    if(snapshot.getValue(String.class).contains("guest:")){
                        button.setEnabled(true);
                        Toast.makeText(RoomActivity.this,"" + snapshot.getValue(String.class).replace("guest:",""),Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }
                else {
                    if(snapshot.getValue(String.class).contains("host:")){
                        button.setEnabled(true);
                        Toast.makeText(RoomActivity.this,"" + snapshot.getValue(String.class).replace("host:",""),Toast.LENGTH_SHORT).show();

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