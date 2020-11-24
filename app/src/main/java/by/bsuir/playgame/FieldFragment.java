package by.bsuir.playgame;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;


public class FieldFragment extends Fragment implements FieldAdapter.ItemListener{


    RecyclerView recyclerView;
    GridLayoutManager layoutManager;

    private String nameList[] = new String[100];
    private int iconId[]= new int[100];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewHierarchy = inflater.inflate(R.layout.fragment_field, container, false);


        recyclerView = viewHierarchy.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getContext(),10);
        recyclerView.setLayoutManager(layoutManager);
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
            {
                nameList[i*10+j] = String.valueOf(i)+ j;
                iconId[i*10+j] = R.drawable._;
            }
        FieldAdapter fieldAdapter =new FieldAdapter(getContext(),iconId,nameList, this);
        recyclerView.setAdapter(fieldAdapter);

        return viewHierarchy;
    }

    @Override
    public void onItemClick(String idField) {

        Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(),  idField + " is clicked", Toast.LENGTH_SHORT).show();

    }
}