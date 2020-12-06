package by.bsuir.playgame.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import by.bsuir.playgame.Adapter.StaticAdapter;
import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.StatisticViewModel;


public class StatisticActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    ListView statisticList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        StatisticViewModel statisticViewModel = ViewModelProviders.of(this).get(StatisticViewModel.class);

        statisticList = (ListView) findViewById(R.id.statisticList);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        statisticViewModel.getListStatistics().observe(this, list -> {
            StaticAdapter stateAdapter = new StaticAdapter(getApplicationContext(), R.layout.statistic_item, list);
            statisticList.setAdapter(stateAdapter);
            progressDialog.dismiss();
        });
        statisticViewModel.readAllName();
    }
}