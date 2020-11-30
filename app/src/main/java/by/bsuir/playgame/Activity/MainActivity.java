package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import by.bsuir.playgame.R;

public class MainActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.Password);

        findViewById(R.id.login).setOnClickListener(v -> Login());
        findViewById(R.id.SignUpTv).setOnClickListener(v -> {
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
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Sign in fail", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        });

    }
}