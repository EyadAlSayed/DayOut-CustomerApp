package com.example.dayout.ui.fragments.drawer.Posts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.dayout.R;
import com.example.dayout.adapters.recyclers.TripPostAdapter;
import com.example.dayout.helpers.view.FN;
import com.example.dayout.models.trip.TripPaginationModel;
import com.example.dayout.models.trip.tripType.TripType;
import com.example.dayout.models.trip.tripType.TripTypeModel;
import com.example.dayout.viewModels.TripViewModel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class FilterPostFragment extends Fragment {

    View view;

    @BindView(R.id.filter_place_name)
    EditText filterPlaceName;

    @BindView(R.id.filter_title)
    EditText filterTitle;

    @BindView(R.id.filter_min_price)
    EditText filterMinPrice;

    @BindView(R.id.filter_max_price)
    EditText filterMaxPrice;

    @BindView(R.id.filter_spinner)
    Spinner filterSpinner;

    @BindView(R.id.filter_button)
    Button filterButton;

    public static boolean isFilterOpen = false;

    TripPostAdapter tripPostAdapter;

    public FilterPostFragment(TripPostAdapter tripPostAdapter) {
        this.tripPostAdapter = tripPostAdapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_fragment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        getDataFromApi();
        return view;
    }

    private void initViews() {
        filterButton.setOnClickListener(onFilterClicked);
    }

    private void initSpinner(String[] spinnerItem) {
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerItem);

        filterSpinner.setAdapter(typesAdapter);
    }

    private void getDataFromApi() {
        TripViewModel.getINSTANCE().getTripType();
        TripViewModel.getINSTANCE().tripTypeTripMutableLiveData.observe(requireActivity(), tripTypeObserver);
    }

    private final Observer<Pair<TripTypeModel, String>> tripTypeObserver = typeStringPair -> {
        if (typeStringPair != null) {
            if (typeStringPair.first != null) {
             initSpinner(getDataName(typeStringPair.first.data));
            }
        }
    };

    private String[] getDataName(List<TripType> list){
        List<String> names = new ArrayList<>();
        names.add(getResources().getString(R.string.any));
        for (TripType t : list){
            names.add(t.name);
        }

        return names.toArray(new String[0]);
    }

    private final View.OnClickListener onFilterClicked = v -> {
        sendSearchRequest();
        isFilterOpen = false;
        FN.popTopStack(requireActivity());
    };

    private void sendSearchRequest() {
        TripViewModel.getINSTANCE().searchForTrip(getSearchObject());
        TripViewModel.getINSTANCE().tripPostMutableLiveData.observe(requireActivity(),tripFilterObserver);
    }

    private final Observer<Pair<TripPaginationModel,String>> tripFilterObserver = new Observer<Pair<TripPaginationModel, String>>() {
        @Override
        public void onChanged(Pair<TripPaginationModel, String> tripPostStringPair) {
            if (tripPostStringPair != null){
                if (tripPostStringPair.first != null){
                    tripPostAdapter.refresh(tripPostStringPair.first.data.data);
                    FN.popTopStack(requireActivity());
                }
            }
        }
    };

    private JsonObject getSearchObject() {
        JsonObject jsonObject = new JsonObject();

        if (!filterPlaceName.getText().toString().isEmpty()){
            jsonObject.addProperty("place", filterPlaceName.getText().toString());
        }

        if (!filterTitle.getText().toString().isEmpty())
            jsonObject.addProperty("title", filterTitle.getText().toString());

        if (!filterSpinner.getSelectedItem().toString().isEmpty())
            jsonObject.addProperty("type", filterSpinner.getSelectedItem().toString());

        if (!filterMinPrice.getText().toString().isEmpty())
            jsonObject.addProperty("min_price", Integer.parseInt(filterMinPrice.getText().toString()));

        if (!filterMaxPrice.getText().toString().isEmpty())
            jsonObject.addProperty("max_price", Integer.parseInt(filterMaxPrice.getText().toString()));

        return jsonObject;

    }


}
