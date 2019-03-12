package com.sap.uncolor.equalizer.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.auth_activity.AuthActivity;
import com.sap.uncolor.equalizer.services.download.NewMusicService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment implements SettingsFragmentContract.View {

    public static final String ACTION_CLEAR_CACHE = "com.example.uncolor.action.CLEAR_CACHE";

    @BindView(R.id.textViewName)
    TextView textViewName;

    @BindView(R.id.imageViewAvatar)
    CircleImageView imageViewAvatar;

    private SettingsFragmentPresenter presenter;

    public static SettingsFragment newInstance() {
        App.Log("init settings fragment");
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        presenter = new SettingsFragmentPresenter(getContext(), this);
        presenter.onLoadUserInfo();
        return view;
    }

    @OnClick(R.id.buttonClearCache)
    void onButtonClearCacheClick() {
        presenter.showClearCacheDialog();
    }

    @OnClick(R.id.buttonExit)
    void onButtonExitClick() {
        presenter.showExitDialog();
    }

    @Override
    public void showUserInfo(String name, String avatarUrl) {
        textViewName.setText(name);
        Glide.with(App.getContext())
                .load(avatarUrl)
                .into(imageViewAvatar);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void logOut() {
        if (getActivity() != null) {
            App.logOut();
            getActivity().stopService(new Intent(getContext(), NewMusicService.class));
            getActivity().finishAffinity();
            startActivity(AuthActivity.getInstance(getContext()));
        }
    }
}
