package by.bsuir.playgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.ButtleViewModel;
import by.bsuir.playgame.ViewModel.DisplayViewModel;

public class RoomActivity extends AppCompatActivity {


    View fieldFragment1, fieldFragment2;
    TextView textView;
    String roomName;

    ButtleViewModel buttleViewModel;
    DisplayViewModel displayViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent postIntent = getIntent();
        roomName = postIntent.getStringExtra(DashboardActivity.PARAM_INTENT_NAME_OF_ROOM);

        buttleViewModel = ViewModelProviders.of(this).get(ButtleViewModel.class);
        displayViewModel = ViewModelProviders.of(this).get(DisplayViewModel.class);

        textView = findViewById(R.id.namePlayer);
        fieldFragment1 = findViewById(R.id.fragment1);
        fieldFragment2 = findViewById(R.id.fragment2);

        buttleViewModel.Init(roomName);
        displayViewModel.Init(roomName);

        buttleViewModel.getInformation().observe(this, text -> textView.setText(text));

        buttleViewModel.getFinish().observe(this, text -> {
            Toast.makeText(RoomActivity.this, getString(R.string.you_win), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        });

        buttleViewModel.getToast().observe(this, text -> Toast.makeText(RoomActivity.this, text, Toast.LENGTH_LONG).show());

        displayViewModel.getFinish().observe(this, text -> {
            textView.setText(getString(R.string.You_lose));
            Toast.makeText(RoomActivity.this, getString(R.string.You_lose), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        });

        displayViewModel.getRestShip().observe(this, value -> buttleViewModel.setRestShip(value));

    }

}