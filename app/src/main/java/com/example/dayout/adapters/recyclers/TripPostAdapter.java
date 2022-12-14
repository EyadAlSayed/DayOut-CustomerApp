package com.example.dayout.adapters.recyclers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dayout.R;
import com.example.dayout.helpers.view.FN;
import com.example.dayout.models.poll.PollsData;
import com.example.dayout.models.room.pollsRoom.databases.PollsDatabase;
import com.example.dayout.models.room.tripsRoom.database.TripsDatabase;
import com.example.dayout.models.trip.TripData;
import com.example.dayout.models.trip.TripPhotoData;
import com.example.dayout.ui.activities.MainActivity;
import com.example.dayout.ui.fragments.trips.details.TripDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.dayout.api.ApiClient.BASE_URL;
import static com.example.dayout.config.AppConstants.MAIN_FRC;
import static com.example.dayout.helpers.view.ImageViewer.IMAGE_BASE_URL;

public class TripPostAdapter extends RecyclerView.Adapter<TripPostAdapter.ViewHolder> {

    List<TripData> list;
    Context context;

    public TripPostAdapter(List<TripData> list, Context context) {
        this.list = list;
        this.context = context;
    }


    public void refresh(List<TripData> list) {
        this.list = list;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        insertRoomObject(list.get(position));

        holder.tripTitle.setText(list.get(position).title);
        holder.tripDate.setText(list.get(position).begin_date + "  -  " + list.get(position).expire_date);
        holder.endTripBookinDate.setText(list.get(position).end_booking);
        holder.tripDescription.setText(list.get(position).description);
        holder.bindImageSlider(list.get(position).trip_photos);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void insertRoomObject(TripData trip) {

        // insert object in room database
        TripsDatabase.getINSTANCE(context).iTrip()
                .insertTrip(trip)
                .subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("Trip Poll Adapter", "onError: " + e.toString());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.trip_title)
        TextView tripTitle;
        @BindView(R.id.trip_image_slider)
        ImageSlider tripImageSlider;
        @BindView(R.id.trip_description)
        TextView tripDescription;
        @BindView(R.id.trip_stops)
        TextView tripStops;
        @BindView(R.id.trip_date)
        TextView tripDate;
        @BindView(R.id.trip_passengers_count)
        TextView tripPassengersCount;
        @BindView(R.id.end_trip_bookin_date)
        TextView endTripBookinDate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bindImageSlider(List<TripPhotoData> photos) {
            List<SlideModel> slideModels = new ArrayList<>();

            for (TripPhotoData ph : photos) {
                slideModels.add(new SlideModel(IMAGE_BASE_URL+ph.path
                        , ScaleTypes.FIT));
            }

            tripImageSlider.setImageList(slideModels);

            tripImageSlider.setScrollBarFadeDuration(10000);
        }

        @Override
        public void onClick(View v) {
            FN.addFixedNameFadeFragment(MAIN_FRC, (MainActivity) context, new TripDetailsFragment(list.get(getAdapterPosition()), true));
        }
    }
}
