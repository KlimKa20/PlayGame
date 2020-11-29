package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import by.bsuir.playgame.R;

public class UserPageActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference myRef;
    private StorageReference reference;
    private Uri imgUri;

    ImageView imageView;
    Button button;
    EditText editText;

    public static final String STORAGE_PATH = "image/";
    public static final int REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        editText = findViewById(R.id.NameUser);
        button = findViewById(R.id.ApplyName);
        imageView = findViewById(R.id.imageView);

        reference = firebaseStorage.getReference();
        myRef = database.getReference("Users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (Objects.equals(child.getKey(), "userName")) {
                        editText.setHint(Objects.requireNonNull(child.getValue()).toString());
                    } else {
                        Glide.with(getApplicationContext())
                                .load(Objects.requireNonNull(child.getValue()).toString())
                                .into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                Query query = database.getReference().child("Users").orderByChild("userName").equalTo(editText.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            myRef.setValue(editText.getText().toString());
                            Toast.makeText(UserPageActivity.this, "Changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserPageActivity.this, "This name already exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Toast.makeText(UserPageActivity.this, "Field is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnBrowse_Click(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public void btnUpload_Click(View v) {
        if (imgUri != null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading Image");
            dialog.show();

            StorageReference ref = reference.child(STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            ref.putFile(imgUri).addOnSuccessListener(taskSnapshot -> {

                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("Image", uri.toString());
                        myRef.updateChildren(childUpdates);
                    }
                });

            })
                    .addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded" + (int) progress + "%");
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();

        }
    }
}