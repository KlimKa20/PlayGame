package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import by.bsuir.playgame.R;

public class MainActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button SignInButton;
    private TextView SignUntv;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.Password);
        SignInButton = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        SignUntv = findViewById(R.id.SignUpTv);
        SignInButton.setOnClickListener(v -> {
            Login();
        });
        SignUntv.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void Login() {
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Enter your Email");
            return;
        } else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Enter your Password");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Successfully Login", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//                Intent intent = new Intent(MainActivity.this, RoomActivity.class);
//                intent.putExtra("roomName","-MN7uyJtGzE0_p_akXUG");
//                intent.putExtra("ViewModel", "Placement1");
                startActivity(intent);
//                finish();
            } else {
                Toast.makeText(MainActivity.this, "Sign in fail", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        });

    }
}