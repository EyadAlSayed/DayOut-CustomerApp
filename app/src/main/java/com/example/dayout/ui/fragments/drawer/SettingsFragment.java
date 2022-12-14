package com.example.dayout.ui.fragments.drawer;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.dayout.R;
import com.example.dayout.config.AppSharedPreferences;
import com.example.dayout.helpers.view.FN;
import com.example.dayout.helpers.view.NoteMessage;
import com.example.dayout.ui.activities.MainActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dayout.api.ApiClient.BASE_URL;

@SuppressLint("NonConstantResourceId")
public class SettingsFragment extends Fragment {


    View view;

    @BindView(R.id.back_arrow)
    ImageButton backArrow;

    @BindView(R.id.base_url)
    EditText baseUrl;

    @BindView(R.id.confirm_button)
    Button confirmButton;

    @BindView(R.id.language_switch)
    Switch languageSwitch;

    int type;

    public SettingsFragment(int type) {
        this.type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onStart() {
        ((MainActivity)requireActivity()).hideBottomBar();
        super.onStart();
    }

    private void initView() {
        baseUrl.setText(BASE_URL);
        confirmButton.setOnClickListener(onConfirmClicked);
        backArrow.setOnClickListener(v -> FN.popTopStack(requireActivity()));
        languageSwitch.setChecked(AppSharedPreferences.GET_CACHE_LAN().equals("en"));
        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (type != 1) {
                if (isChecked) {
                    ((MainActivity) requireActivity()).changeLanguage("en", true);
                } else {
                    ((MainActivity) requireActivity()).changeLanguage("ar", true);
                }
            }
        });
    }

    private final View.OnClickListener onConfirmClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppSharedPreferences.CACHE_BASE_URL(baseUrl.getText().toString());
            NoteMessage.showSnackBar(requireActivity(), BASE_URL);
            FN.popTopStack(requireActivity());
        }
    };


}