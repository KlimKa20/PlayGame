package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.ShipViewModel;

import static by.bsuir.playgame.Enum.ShipType.FOUR_SECTION;
import static by.bsuir.playgame.Enum.ShipType.ONE_SECTION;
import static by.bsuir.playgame.Enum.ShipType.THREE_SECTION;
import static by.bsuir.playgame.Enum.ShipType.TWO_SECTION;

public class PlacementRoomActivity extends AppCompatActivity {

    ShipViewModel shipViewModel;
    Button button5, button4, button3, button2, button1, buttleButton;
    TextView textView4, textView3, textView2, textView1;
    String roomName;

    private ProgressDialog progressDialog;

    public static final int REST_SHIPS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent postIntent = getIntent();
        roomName = postIntent.getStringExtra(DashboardActivity.PARAM_INTENT_NAME_OF_ROOM);
        progressDialog = new ProgressDialog(this);

        shipViewModel = ViewModelProviders.of(this).get(ShipViewModel.class);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        buttleButton = findViewById(R.id.buttle);
        textView1 = findViewById(R.id.textView6);
        textView2 = findViewById(R.id.textView5);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView2);

        button1.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(ONE_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();
        });
        button2.setOnClickListener(item -> {
            if (Integer.parseInt(textView2.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(TWO_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();
        });
        button3.setOnClickListener(item -> {
            if (Integer.parseInt(textView3.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(THREE_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();
        });
        button4.setOnClickListener(item -> {
            if (Integer.parseInt(textView4.getText().toString()) > REST_SHIPS)
                shipViewModel.setShip(FOUR_SECTION);
            else
                Toast.makeText(this, getString(R.string.Choose_other_type_of_ship), Toast.LENGTH_SHORT).show();
        });

        buttleButton.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) == REST_SHIPS) {
                Toast.makeText(this, getString(R.string.Fight), Toast.LENGTH_SHORT).show();
                shipViewModel.buttle();
            } else {
                Toast.makeText(this, getString(R.string.set_not_all_ships), Toast.LENGTH_SHORT).show();
            }
        });

        button5.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString()) +
                    Integer.parseInt(textView2.getText().toString()) +
                    Integer.parseInt(textView3.getText().toString()) +
                    Integer.parseInt(textView4.getText().toString()) != DashboardActivity.MAX_SHIP_COUNT) {
                shipViewModel.deleteShip();
            } else {
                Toast.makeText(this, getString(R.string.Field_is_empty), Toast.LENGTH_SHORT).show();
            }
        });

        shipViewModel.initPage(roomName);

        shipViewModel.getError().observe(this, s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());

        shipViewModel.getShowDialog().observe(this, text -> {
            progressDialog.setMessage(text);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        });

        shipViewModel.getStartGame().observe(this, status -> {
            if (status) {
                progressDialog.dismiss();
                Intent intent = new Intent(PlacementRoomActivity.this, RoomActivity.class);
                intent.putExtra(DashboardActivity.PARAM_INTENT_NAME_OF_ROOM, roomName);
                intent.putExtra(DashboardActivity.PARAM_INTENT_TYPE_VIEWMODEL, DashboardActivity.TYPE_VIEWMODEL_BATTLE);
                startActivity(intent);
                finish();
            } else {
                progressDialog.dismiss();
            }
        });

        shipViewModel.getResultOfSetShip().observe(this, size -> {
            switch (size) {
                case -4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) + 1));
                    break;
                case -3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) + 1));
                    break;
                case -2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) + 1));
                    break;
                case -1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString()) + 1));
                    break;
                case 1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString()) - 1));
                    break;
                case 2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString()) - 1));
                    break;
                case 3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString()) - 1));
                    break;
                case 4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString()) - 1));
                    break;
            }
        });
    }
}