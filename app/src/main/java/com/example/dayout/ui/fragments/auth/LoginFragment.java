package com.example.dayout.ui.fragments.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.dayout.R;
import com.example.dayout.config.AppSharedPreferences;
import com.example.dayout.helpers.view.FN;
import com.example.dayout.models.authModels.LoginModel;
import com.example.dayout.ui.activities.AuthActivity;
import com.example.dayout.ui.activities.MainActivity;
import com.example.dayout.ui.dialogs.notify.ErrorDialog;
import com.example.dayout.ui.dialogs.notify.LoadingDialog;
import com.example.dayout.ui.fragments.auth.forgetPassword.ForgetPasswordFragment;
import com.example.dayout.viewModels.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dayout.config.AppConstants.AUTH_FRC;

@SuppressLint("NonConstantResourceId")
public class LoginFragment extends Fragment {


    View view;

    @BindView(R.id.create_account_txt)
    TextView createAccountTxt;

    @BindView(R.id.user_name)
    TextInputEditText userName;

    @BindView(R.id.user_name_textlayout)
    TextInputLayout userNameTextlayout;

    @BindView(R.id.password)
    TextInputEditText password;

    @BindView(R.id.password_textlayout)
    TextInputLayout passwordTextlayout;

    @BindView(R.id.remember_me_switch)
    Switch rememberMeSwitch;

    @BindView(R.id.login_btn)
    Button loginButton;

    @BindView(R.id.forgetpassword)
    TextView forgetpassword;


    LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        loadingDialog = new LoadingDialog(requireContext());
        loginButton.setOnClickListener(onLoginClicked);
        createAccountTxt.setOnClickListener(onCreateClicked);
        password.addTextChangedListener(onTextChanged);
        forgetpassword.setOnClickListener(onForgetClicked);
    }


    private final View.OnClickListener onLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkInfo()) {
                loadingDialog.show();
                AuthViewModel.getINSTANCE().login(getLoginInfo());
                AuthViewModel.getINSTANCE().loginMutableLiveData.observe(requireActivity(), loginObserver);
            }
        }
    };

    private final Observer<Pair<LoginModel, String>> loginObserver = new Observer<Pair<LoginModel, String>>() {
        @Override
        public void onChanged(Pair<LoginModel, String> loginModelStringPair) {
            loadingDialog.dismiss();
            if (loginModelStringPair != null) {
                if (loginModelStringPair.first != null) {
                    cacheData(loginModelStringPair.first.data.id,loginModelStringPair.first.data.token);
                    openMainActivity();
                } else new ErrorDialog(requireContext(), loginModelStringPair.second).show();
            } else new ErrorDialog(requireContext(), getResources().getString(R.string.error_connection)).show();
        }
    };

    private final View.OnClickListener onCreateClicked = v -> FN.addFixedNameFadeFragment(AUTH_FRC, requireActivity(), new SignUpFragment());
    private final View.OnClickListener onForgetClicked = v -> FN.addFixedNameFadeFragment(AUTH_FRC, requireActivity(), new ForgetPasswordFragment());
    private final TextWatcher onTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passwordTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void cacheData(int id,String token){
        AppSharedPreferences.CACHE_REMEMBER_ME(rememberMeSwitch.isChecked());
        AppSharedPreferences.CACHE_AUTH_DATA(id, token);
    }

    private JsonObject getLoginInfo() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone_number", userName.getText().toString());
        jsonObject.addProperty("password", password.getText().toString());
        return jsonObject;
    }

    private void openMainActivity() {
        Intent intent = new Intent(((AuthActivity)requireActivity()), MainActivity.class);
        ((AuthActivity)requireActivity()).startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ((AuthActivity)requireActivity()).finishActivity();

    }

    private boolean checkInfo() {

        boolean ok = true;
        if (userName.getText().toString().isEmpty()) {
            ok = false;
            userNameTextlayout.setErrorEnabled(true);
            userNameTextlayout.setError(getResources().getString(R.string.required));
        } else if (!checkSyrianNumber()) {
            ok = false;
            userNameTextlayout.setErrorEnabled(true);
            userNameTextlayout.setError(getResources().getString(R.string.not_a_phone_number));
        }


        if (password.getText().toString().isEmpty()) {
            ok = false;
            passwordTextlayout.setErrorEnabled(true);
            passwordTextlayout.setError(getResources().getString(R.string.required));
        } else passwordTextlayout.setErrorEnabled(false);

        return ok;
    }

    private boolean checkSyrianNumber() {
        String _userName = userName.getText().toString();
        if (_userName.length() < 10) return false;
        if (_userName.contains("+")) return false;
        if (_userName.charAt(0) != '0' && _userName.charAt(1) != '9') return false;
        return true;
    }

}