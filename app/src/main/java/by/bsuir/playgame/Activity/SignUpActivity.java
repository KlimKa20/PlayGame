package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.bsuir.playgame.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        emailEt = findViewById(R.id.Email);
        passwordEt1 = findViewById(R.id.Password1);
        passwordEt2 = findViewById(R.id.Password2);

        findViewById(R.id.register).setOnClickListener(v -> Register());
        findViewById(R.id.signInTv).setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void Register() {
        String email = emailEt.getText().toString();
        String password1 = passwordEt1.getText().toString();
        String password2 = passwordEt2.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Enter your Email");
            return;
        } else if (TextUtils.isEmpty(password1)) {
            passwordEt1.setError("Enter your Password");
            return;
        } else if (TextUtils.isEmpty(password2)) {
            passwordEt2.setError("Confirm your Password");
            return;
        } else if (!password1.equals(password2)) {
            passwordEt2.setError("Different password");
            return;
        } else if (password1.length() < 4) {
            passwordEt1.setError("Length should be >4");
            return;
        } else if (!isValidEmail(email)) {
            emailEt.setError("Invalid email");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                DatabaseReference myRef = database.getReference("Users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                Map<String, Object> values = new HashMap<>();
                values.put("Gravatar", false);
                values.put("userName", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                values.put("Image", "https://firebasestorage.googleapis.com/v0/b/playgamekl.appspot.com/o/image%2F1606652344403.png?alt=media&token=e5c51ce4-3939-490e-9d4f-fc5ddeef127e");
                myRef.updateChildren(values);
                database.getReference().updateChildren(values);
                Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Sign up fail", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        });
    }

    private Boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
