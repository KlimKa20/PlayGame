package by.bsuir.playgame.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.UserPageViewModel;

public class UserPageActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private UserPageViewModel userPageViewModel;

    ImageView imageView;
    Button setNewName;
    EditText editText;

    public static final String STORAGE_PATH = "image/";
    public static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        userPageViewModel = ViewModelProviders.of(this).get(UserPageViewModel.class);

        editText = findViewById(R.id.NameUser);
        setNewName = findViewById(R.id.ApplyName);
        imageView = findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        userPageViewModel.isGravatar().observe(this, status -> {
            if (status) {
                ((RadioButton) findViewById(R.id.GravatarButton)).setChecked(true);
                findViewById(R.id.ChooseImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.INVISIBLE);
            } else {
                ((RadioButton) findViewById(R.id.FireBaseButton)).setChecked(true);
                findViewById(R.id.ChooseImage).setVisibility(View.VISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.VISIBLE);
            }
        });

        userPageViewModel.getImage().observe(this, image -> {
            Picasso.with(getApplicationContext())
                    .load(image)
                    .into(imageView);
            progressDialog.dismiss();
        });

        userPageViewModel.getUserName().observe(this, userName -> editText.setHint(userName));
        userPageViewModel.getDialogMessage().observe(this, text -> progressDialog.setMessage(text));
        userPageViewModel.getOutputMessage().observe(this, text -> {
            progressDialog.dismiss();
            Toast.makeText(UserPageActivity.this, text, Toast.LENGTH_SHORT).show();
        });

        userPageViewModel.getImgUri().observe(this, uri -> {
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        userPageViewModel.setInformation();

        setNewName.setOnClickListener(v -> userPageViewModel.setNewName(editText.getText().toString()));
    }

    public void btnBrowse_Click(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.Select_image)), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            userPageViewModel.setImgUri(data.getData());
        }
    }

    public void btnUpload_Click(View v) {
        progressDialog.setTitle(getString(R.string.Uploading_Image));
        progressDialog.show();
        userPageViewModel.uploadImage();
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.GravatarButton:
                findViewById(R.id.ChooseImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.INVISIBLE);
                userPageViewModel.changeButton(true);
                break;
            case R.id.FireBaseButton:
                findViewById(R.id.ChooseImage).setVisibility(View.VISIBLE);
                findViewById(R.id.UpdateImage).setVisibility(View.VISIBLE);
                userPageViewModel.changeButton(false);
                break;
        }
    }
}