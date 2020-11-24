package by.bsuir.playgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserPageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    Button button;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        editText = findViewById(R.id.NameUser);
        button = findViewById(R.id.ApplyName);

        myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("userName");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editText.setHint(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        button.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()){
                Query query = database.getReference().child("Users").orderByChild("userName").equalTo(editText.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) {
                            myRef.setValue(editText.getText().toString());
                            Toast.makeText(UserPageActivity.this,"Changed successfully",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(UserPageActivity.this,"This name already exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(UserPageActivity.this,"Field is empty!",Toast.LENGTH_SHORT).show();
            }

        });
    }

}