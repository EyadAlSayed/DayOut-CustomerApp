package com.example.dayout.ui.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dayout.R;
import com.example.dayout.models.PopularPlace;
import com.example.dayout.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceInfoFragment extends Fragment {


    View view;
    @BindView(R.id.image_slider)
    ImageSlider imageSlider;
    @BindView(R.id.place_full_info_txt)
    TextView placeFullInfoTxt;
    @BindView(R.id.short_descrption)
    TextView shortDescrption;

    PopularPlace.Data popularPlaceData;


    public PlaceInfoFragment(PopularPlace.Data popularPlaceData) {
        this.popularPlaceData = popularPlaceData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_place_info, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onStart() {
        ((MainActivity) requireActivity()).hideBottomBar();
        super.onStart();
    }

    private void initView() {
        initImageSlider();
        placeFullInfoTxt.setText(popularPlaceData.description);
        shortDescrption.setText(popularPlaceData.summary);
    }

    private void initImageSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        for (PopularPlace.Photo ph : popularPlaceData.photos) {
            slideModels.add(new SlideModel(ph.path, ScaleTypes.FIT));
        }
        imageSlider.setImageList(slideModels);
        imageSlider.setScrollBarFadeDuration(10000);
    }
}