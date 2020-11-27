package by.bsuir.playgame.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import by.bsuir.playgame.Adapter.FieldAdapter;
import by.bsuir.playgame.Interfece.IFieldViewModel;
import by.bsuir.playgame.R;
import by.bsuir.playgame.ViewModel.ButtleViewModel;
import by.bsuir.playgame.ViewModel.ShipViewModel;


public class FieldFragment extends Fragment implements FieldAdapter.ItemListener {


    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    IFieldViewModel shipViewModel;

    private String nameList[] = new String[100];
    private int iconId[] = new int[100];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = Objects.requireNonNull(getActivity()).getIntent();
        if (intent.getStringExtra("ViewModel").equals("Placement")) {
            shipViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ShipViewModel.class);
        } else {
            shipViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtleViewModel.class);
        }

        shipViewModel.getIcon().observe(Objects.requireNonNull(requireActivity()), s -> {
            FieldAdapter fieldAdapter = new FieldAdapter(getContext(), s, nameList, this);
            recyclerView.setAdapter(fieldAdapter);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewHierarchy = inflater.inflate(R.layout.fragment_field, container, false);

        recyclerView = viewHierarchy.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getContext(), 10);
        recyclerView.setLayoutManager(layoutManager);
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                nameList[i * 10 + j] = String.valueOf(i) + j;
            }
        FieldAdapter fieldAdapter = new FieldAdapter(getContext(), iconId, nameList, this);
        recyclerView.setAdapter(fieldAdapter);
        return viewHierarchy;
    }

    @Override
    public void onItemClick(String idField) {
        shipViewModel.setPoint(idField);
    }

}