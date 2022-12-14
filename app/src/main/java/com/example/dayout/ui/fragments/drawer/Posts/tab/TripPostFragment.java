package com.example.dayout.ui.fragments.drawer.Posts.tab;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dayout.R;
import com.example.dayout.adapters.recyclers.TripPostAdapter;
import com.example.dayout.helpers.view.FN;
import com.example.dayout.models.room.tripsRoom.database.TripsDatabase;
import com.example.dayout.models.trip.TripData;
import com.example.dayout.models.trip.TripPaginationModel;
import com.example.dayout.ui.activities.MainActivity;
import com.example.dayout.ui.dialogs.notify.ErrorDialog;
import com.example.dayout.ui.fragments.drawer.Posts.FilterPostFragment;
import com.example.dayout.viewModels.TripViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.dayout.config.AppConstants.MAIN_FRC;

@SuppressLint("NonConstantResourceId")
public class TripPostFragment extends Fragment {


    View view;

    @BindView(R.id.filter_btn)
    ImageButton filterBtn;

    @BindView(R.id.trip_poll_post_rc)
    RecyclerView tripPostRc;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.page_loading_pbar)
    ProgressBar pageLoadingBar;

    TripPostAdapter tripPostAdapter;

    int pageNumber;
    boolean canPaginate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trip_poll_post, container, false);
        ButterKnife.bind(this, view);
        initView();
        getDataFromApi();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).hideBottomBar();
    }

    private void initView() {
        pageNumber = 1;
        filterBtn.setOnClickListener(onFilterClicked);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        initRc();
    }

    private void getDataFromRoom() {
        TripsDatabase.getINSTANCE(requireContext())
                .iTrip()
                .getTrips()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TripData>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull List<TripData> data) {
                        tripPostAdapter.refresh(data);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e("getter polls roomDB", "onError: " + e.toString());
                    }
                });
    }

    private void getDataFromApi() {
        TripViewModel.getINSTANCE().getTripPost(pageNumber);
        TripViewModel.getINSTANCE().tripPostMutableLiveData.observe(requireActivity(), tripPostObserver);
    }

    private final Observer<Pair<TripPaginationModel, String>> tripPostObserver = new Observer<Pair<TripPaginationModel, String>>() {
        @Override
        public void onChanged(Pair<TripPaginationModel, String> tripPostStringPair) {
            if (tripPostStringPair != null) {
                if (tripPostStringPair.first != null) {
                    tripPostAdapter.refresh(tripPostStringPair.first.data.data);
                    canPaginate = (tripPostStringPair.first.data.next_page_url != null);
                } else {
                    getDataFromRoom();
                    new ErrorDialog(requireContext(), tripPostStringPair.second).show();
                }
            } else {
                getDataFromRoom();
                new ErrorDialog (requireContext(), getResources().getString(R.string.error_connection)).show();
            }

            hideLoadingBar();
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);
        }
    };

    private void initRc() {
        tripPostRc.setHasFixedSize(true);
        tripPostRc.addOnScrollListener(onRcScrolled);
        tripPostRc.setLayoutManager(new LinearLayoutManager(requireContext()));
        tripPostAdapter = new TripPostAdapter(new ArrayList<>(), requireContext());
        tripPostRc.setAdapter(tripPostAdapter);
    }

    private final View.OnClickListener onFilterClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FN.addToStackSlideUDFragment(MAIN_FRC, requireActivity(), new FilterPostFragment(tripPostAdapter), "filter_post");
        }
    };

    private final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pageNumber = 1;
            swipeRefreshLayout.setEnabled(false);
            getDataFromApi();
        }
    };


    // pagination method

    private void hideLoadingBar() {
        if (pageLoadingBar.getVisibility() == View.GONE) return;

        pageLoadingBar.animate().setDuration(400).alpha(0);
        new Handler(Looper.getMainLooper()).postDelayed(() -> pageLoadingBar.setVisibility(View.GONE), 450);
    }

    private void showLoadingBar() {
        if (pageLoadingBar.getVisibility() == View.VISIBLE) return;

        pageLoadingBar.setAlpha(1);
        pageLoadingBar.setVisibility(View.VISIBLE);
    }


    private final RecyclerView.OnScrollListener onRcScrolled = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == 1 && canPaginate) {    // is scrolling
                pageNumber++;
                showLoadingBar();
                getDataFromApi();
                canPaginate = false;
            }

            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            super.onScrolled(recyclerView, dx, dy);
        }
    };


}