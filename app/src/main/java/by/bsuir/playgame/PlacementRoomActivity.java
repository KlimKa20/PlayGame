package by.bsuir.playgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class PlacementRoomActivity extends AppCompatActivity {

    ShipViewModel shipViewModel;
    Button button5,button4,button3,button2,button1;
    TextView textView4,textView3,textView2,textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_room);
        Objects.requireNonNull(getSupportActionBar()).hide();

        shipViewModel = ViewModelProviders.of(this).get(ShipViewModel.class);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        textView1 = findViewById(R.id.textView6);
        textView2 = findViewById(R.id.textView5);
        textView3 = findViewById(R.id.textView4);
        textView4 = findViewById(R.id.textView2);
        button1.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString())>0)
                shipViewModel.setShip("1");
        });
        button2.setOnClickListener(item -> {
            if (Integer.parseInt(textView2.getText().toString())>0)
                shipViewModel.setShip("2");
        });
        button3.setOnClickListener(item -> {
            if (Integer.parseInt(textView3.getText().toString())>0)
                shipViewModel.setShip("3");
        });
        button4.setOnClickListener(item -> {
            if (Integer.parseInt(textView4.getText().toString())>0)
                shipViewModel.setShip("4");
        });

        button5.setOnClickListener(item -> {
            if (Integer.parseInt(textView1.getText().toString())+
                Integer.parseInt(textView2.getText().toString())+
                Integer.parseInt(textView3.getText().toString())+
                Integer.parseInt(textView4.getText().toString()) != 10) {
                shipViewModel.setShip("-1");
            }
            else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        shipViewModel.getResultOfSetShip().observe(this, s -> {
            switch (s){
                case 1:
                    textView1.setText(String.valueOf(Integer.parseInt(textView1.getText().toString())-1));
                    break;
                case 2:
                    textView2.setText(String.valueOf(Integer.parseInt(textView2.getText().toString())-1));
                    break;
                case 3:
                    textView3.setText(String.valueOf(Integer.parseInt(textView3.getText().toString())-1));
                    break;
                case 4:
                    textView4.setText(String.valueOf(Integer.parseInt(textView4.getText().toString())-1));
                    break;
            }
        });

    }
}