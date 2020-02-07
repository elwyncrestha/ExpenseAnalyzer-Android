package com.elvin.expense_analyzer.ui.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.ui.activity.LoginActivity;
import com.elvin.expense_analyzer.ui.activity.MainActivity;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnLogout, btnLogoutAll;
        btnLogout = root.findViewById(R.id.btnLogout);
        btnLogoutAll = root.findViewById(R.id.btnLogoutAll);

        final UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "Bearer " + SharedPreferencesUtils.getAuthToken(getContext());
                Call<Void> call = v.getId() == R.id.btnLogout
                        ? userService.logout(token)
                        : userService.logoutAll(token);
                try {
                    Response<Void> response = call.execute();
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(getContext(), "Logout successful", Toast.LENGTH_SHORT).show();

                    SharedPreferencesUtils.setAuthToken(getContext(), null);

                    startActivity(new Intent(getContext(), LoginActivity.class));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Logout Current", "Failed to logout current device", e);
                    Toast.makeText(getContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
                }
            }
        };
        StrictMode.StrictMode();
        btnLogout.setOnClickListener(listener);
        btnLogoutAll.setOnClickListener(listener);

        return root;
    }
}