package com.elvin.expense_analyzer.ui.fragment.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.ui.activity.LoginActivity;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    Button btnLogout, btnLogoutAll, btnProfileSave;
    private TextView tvProfileName;
    private EditText etProfileFirstName, etProfileMiddleName, etProfileLastName, etProfileEmail, etProfileUsername;
    private User authenticatedUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        this.tvProfileName = root.findViewById(R.id.tvProfileName);
        this.etProfileFirstName = root.findViewById(R.id.etProfileFirstName);
        this.etProfileMiddleName = root.findViewById(R.id.etProfileMiddleName);
        this.etProfileLastName = root.findViewById(R.id.etProfileLastName);
        this.etProfileEmail = root.findViewById(R.id.etProfileEmail);
        this.etProfileUsername = root.findViewById(R.id.etProfileUsername);
        this.btnLogout = root.findViewById(R.id.btnLogout);
        this.btnLogoutAll = root.findViewById(R.id.btnLogoutAll);
        this.btnProfileSave = root.findViewById(R.id.btnProfileSave);

        final UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);

        this.loadProfile(userService);

        this.btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileConfigurations(userService);
            }
        });

        this.logoutConfigurations(userService);

        return root;
    }

    private void loadProfile(UserService userService) {
        Call<ResponseDto<User>> userCall = userService.getAuthenticated("Bearer " + SharedPreferencesUtils.getAuthToken(getContext()));
        userCall.enqueue(new Callback<ResponseDto<User>>() {
            @Override
            public void onResponse(Call<ResponseDto<User>> call, Response<ResponseDto<User>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                authenticatedUser = response.body().getDetail();
                fillProfileForm(authenticatedUser);
            }

            @Override
            public void onFailure(Call<ResponseDto<User>> call, Throwable t) {
                Log.e("User Profile", "Failed to load profile", t);
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillProfileForm(User user) {
        tvProfileName.setText(
                String.format(
                        Locale.ENGLISH,
                        "%s %s",
                        user.getFirstName(), user.getLastName()
                )
        );
        etProfileFirstName.setText(user.getFirstName());
        etProfileMiddleName.setText(user.getMiddleName());
        etProfileLastName.setText(user.getLastName());
        etProfileEmail.setText(user.getEmail());
        etProfileUsername.setText(user.getUsername());
    }

    private void updateProfileConfigurations(final UserService userService) {
        String firstName = etProfileFirstName.getText().toString();
        String middleName = etProfileMiddleName.getText().toString();
        String lastName = etProfileLastName.getText().toString();
        String email = etProfileEmail.getText().toString();
        String username = etProfileUsername.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            etProfileFirstName.setError("First name is required");
            return;
        } else if (TextUtils.isEmpty(lastName)) {
            etProfileLastName.setError("Last name is required");
            return;
        } else if (TextUtils.isEmpty(email)) {
            etProfileEmail.setError("Email is required");
            return;
        } else if (TextUtils.isEmpty(username)) {
            etProfileUsername.setError("Username is required");
            return;
        }

        this.authenticatedUser.setFirstName(firstName);
        this.authenticatedUser.setMiddleName(middleName);
        this.authenticatedUser.setLastName(lastName);
        this.authenticatedUser.setEmail(email);
        this.authenticatedUser.setUsername(username);

        userService.update(this.authenticatedUser).enqueue(new Callback<ResponseDto<User>>() {
            @Override
            public void onResponse(Call<ResponseDto<User>> call, Response<ResponseDto<User>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), "Successfully updated profile", Toast.LENGTH_SHORT).show();
                loadProfile(userService);
            }

            @Override
            public void onFailure(Call<ResponseDto<User>> call, Throwable t) {
                Log.e("Update Profile", "Failed to update profile", t);
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutConfigurations(final UserService userService) {
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
    }
}