package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.LoginViewModel;

public class MainActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private ProgressDialog progressDialog;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        progressDialog = new ProgressDialog(this);

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.Password);

        loginViewModel.getEmailError().observe(this, text -> {
            emailEt.setError(text);
            progressDialog.dismiss();
        });
        loginViewModel.getPasswordError().observe(this, text -> {
            passwordEt.setError(text);
            progressDialog.dismiss();
        });
        loginViewModel.isSuccessful().observe(this, status -> {
            if (status) {
                Toast.makeText(MainActivity.this, getString(R.string.Successfully_Login), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.Sign_in_fail), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        });

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
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        loginViewModel.Login(email,password);
    }
}