package by.bsuir.playgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;


public class FieldFragment extends Fragment implements FieldAdapter.ItemListener {


    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    ShipViewModel shipViewModel;

    private String nameList[] = new String[100];
    private int iconId[] = new int[100];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shipViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ShipViewModel.class);
        shipViewModel.getPoint().observe(Objects.requireNonNull(requireActivity()), s -> SetShip(s));
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
                iconId[i * 10 + j] = R.drawable._;
            }
        FieldAdapter fieldAdapter = new FieldAdapter(getContext(), iconId, nameList, this);
        recyclerView.setAdapter(fieldAdapter);
        return viewHierarchy;
    }

    @Override
    public void onItemClick(String idField) {
        shipViewModel.setPoint(idField);
    }

    public void SetShip(String value) {
        if (value.length() == 5) {
            if (!check(Integer.parseInt(value.substring(0, 2))) || !check(Integer.parseInt(value.substring(2, 4)))) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), "error", Toast.LENGTH_SHORT).show();
                return;
            }
            if (value.charAt(0) == value.charAt(2) && Math.abs(value.charAt(1) - value.charAt(3)) == value.charAt(4) - '0' - 1) {
                for (int i = Math.min(value.charAt(1), value.charAt(3)) - '0'; i <= Math.max(value.charAt(1), value.charAt(3)) - '0'; i++) {
                    int ii = (value.charAt(0) - '0') * 10 + i;
                    iconId[ii] = R.drawable.ic_launcher_background;
                }
            } else if (value.charAt(1) == value.charAt(3) && Math.abs(value.charAt(0) - value.charAt(2)) == value.charAt(4) - '0' - 1) {
                for (int i = Math.min(value.charAt(0), value.charAt(2)) - '0'; i <= Math.max(value.charAt(0), value.charAt(2)) - '0'; i++) {
                    iconId[i * 10 + (value.charAt(1) - '0')] = R.drawable.ic_launcher_background;
                }
            } else {
                Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), "error", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (value.length() == 4) {
            deleteShip(Integer.parseInt(value.substring(0, 2)));
        } else {
            if (!check(Integer.parseInt(value.substring(0, 2)))) {
                Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), "error", Toast.LENGTH_SHORT).show();
                return;
            }
            iconId[(value.charAt(0) - '0') * 10 + (value.charAt(1) - '0')] = R.drawable.ic_launcher_background;
        }
        shipViewModel.setResultOfSetShip(value.charAt(value.length() - 1) - '0');
        FieldAdapter fieldAdapter = new FieldAdapter(getContext(), iconId, nameList, this);
        recyclerView.setAdapter(fieldAdapter);
    }

    public int deleteShip(int position) {
        int fullField = iconId[position];
        iconId[position] = R.drawable._;
        int sizeship = 1;
        if (position % 10 == 0 && fullField == iconId[position + 1]) {
            return sizeship + deleteShip(position + 1);
        } else if (position % 10 == 9 && fullField == iconId[position - 1]) {
            return sizeship + deleteShip(position - 1);
        } else {
            if (fullField == iconId[position + 1])
                sizeship += deleteShip(position + 1);
            if (fullField == iconId[position - 1])
                return sizeship + deleteShip(position - 1);
        }
        if (position - 10 < 0 && fullField == iconId[position + 10]) {
            return sizeship + deleteShip(position + 10);
        } else if (position + 10 > 100 && fullField == iconId[position - 10]) {
            return sizeship + deleteShip(position - 10);
        } else {
            if (position < 90 && fullField == iconId[position + 10])
                sizeship += deleteShip(position + 10);
            if (position > 9 && fullField == iconId[position - 10])
                return sizeship + deleteShip(position - 10);
        }
        return fullField;
    }

    public boolean check(int position) {
        int emptyField = iconId[position];
        int[] massCheck;
        if (position % 10 == 0) {
            massCheck = new int[]{
                    position - 10, position - 9,
                    position, position + 1,
                    position + 10, position + 11
            };
        } else if (position % 10 == 9) {
            massCheck = new int[]{
                    position - 11, position - 10,
                    position - 1, position,
                    position + 9, position + 10
            };
        } else {
            massCheck = new int[]{
                    position - 11, position - 10, position - 9,
                    position - 1, position, position + 1,
                    position + 9, position + 10, position + 11
            };
        }
        for (int item : massCheck) {
            if (item < 100 && item >= 0) {
                if (emptyField != iconId[item]) {
                    return false;
                }
            }
        }
        return true;
    }
}