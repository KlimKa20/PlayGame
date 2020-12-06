package by.bsuir.playgame.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.DashboardViewModel;

public class DashboardActivity extends AppCompatActivity {

    public static final int REQUEST_HOST = 103;
    public static final int REQUEST_GUEST = 104;
    public static final int MAX_SHIP_COUNT = 10;

    public static final String PARAM_MENU_USER_PAGE = "User Page";
    public static final String PARAM_MENU_USER_STATISTIC = "User Statistic";
    public static final String PARAM_INTENT_NAME_OF_ROOM = "roomName";
    public static final String PARAM_INTENT_TYPE_VIEWMODEL = "ViewModel";
    public static final String TYPE_VIEWMODEL_PLACEMENT = "Placement";
    public static final String TYPE_VIEWMODEL_BATTLE = "BattleView";

    private EditText textView;
    private DashboardViewModel dashboardViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);

        dashboardViewModel.getOutputMessage().observe(this, text -> Toast.makeText(DashboardActivity.this, text, Toast.LENGTH_SHORT).show());
        dashboardViewModel.getMoveToRoom().observe(this, text -> moveToRoom(Integer.parseInt(text.get("role").toString()), text.get("name").toString()));
        textView = findViewById(R.id.room);

        findViewById(R.id.logout).setOnClickListener(this::onLogOutClick);
        findViewById(R.id.create).setOnClickListener(v -> dashboardViewModel.createRoom(textView.getText().toString()));
        findViewById(R.id.connect).setOnClickListener(v -> dashboardViewModel.connectToRoom(textView.getText().toString()));
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, PARAM_MENU_USER_PAGE);
        menu.add(0, 2, 0, PARAM_MENU_USER_STATISTIC);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case 1:
                intent = new Intent(this, UserPageActivity.class);
                break;
            case 2:
                intent = new Intent(this, StatisticActivity.class);
                break;
            default:
                intent = null;
        }
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void moveToRoom(int index, String key) {
        Intent intent = new Intent(DashboardActivity.this, PlacementRoomActivity.class);
        intent.putExtra(PARAM_INTENT_NAME_OF_ROOM, key);
        intent.putExtra(PARAM_INTENT_TYPE_VIEWMODEL, TYPE_VIEWMODEL_PLACEMENT);
        startActivityForResult(intent, index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_HOST) {
            if (resultCode == 0) {
                Toast.makeText(DashboardActivity.this, getString(R.string.You_lose), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_GUEST) {
            if (resultCode == 0) {
                dashboardViewModel.removeRoom();
                Toast.makeText(DashboardActivity.this, getString(R.string.you_win), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onLogOutClick(View v) {
        dashboardViewModel.signOut();
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
