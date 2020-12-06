package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.RegistratioViewModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private ProgressDialog progressDialog;
    private RegistratioViewModel registratioViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        registratioViewModel = ViewModelProviders.of(this).get(RegistratioViewModel.class);

        emailEt = findViewById(R.id.Email);
        passwordEt1 = findViewById(R.id.Password1);
        passwordEt2 = findViewById(R.id.Password2);

        registratioViewModel.getEmailError().observe(this, text -> {
            emailEt.setError(text);
            progressDialog.dismiss();
        });
        registratioViewModel.getPassword1Error().observe(this, text -> {
            passwordEt1.setError(text);
            progressDialog.dismiss();
        });
        registratioViewModel.getPassword2Error().observe(this, text -> {
            passwordEt2.setError(text);
            progressDialog.dismiss();
        });
        registratioViewModel.isSuccessful().observe(this, status -> {
            if (status) {
                Toast.makeText(SignUpActivity.this, getString(R.string.Successfully_registered), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, getString(R.string.Sign_up_fail), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        });

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
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        registratioViewModel.Register(email, password1, password2);
    }
}
