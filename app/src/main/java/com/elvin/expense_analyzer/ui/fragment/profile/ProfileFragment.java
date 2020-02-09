package com.elvin.expense_analyzer.ui.fragment.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.Build;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.Base64Utils;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private Button btnProfileSave;
    private TextView tvProfileName;
    private EditText etProfileFirstName, etProfileMiddleName, etProfileLastName, etProfileEmail, etProfileUsername;
    private CircularImageView profileImage;

    private User authenticatedUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        this.profileImage = root.findViewById(R.id.profileImage);
        this.tvProfileName = root.findViewById(R.id.tvProfileName);
        this.etProfileFirstName = root.findViewById(R.id.etProfileFirstName);
        this.etProfileMiddleName = root.findViewById(R.id.etProfileMiddleName);
        this.etProfileLastName = root.findViewById(R.id.etProfileLastName);
        this.etProfileEmail = root.findViewById(R.id.etProfileEmail);
        this.etProfileUsername = root.findViewById(R.id.etProfileUsername);
        this.btnProfileSave = root.findViewById(R.id.btnProfileSave);

        final UserService userService = RetrofitUtils.getRetrofit().create(UserService.class);

        this.loadProfile(userService);
        this.uploadProfileImageConfig();

        this.btnProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileConfigurations(userService);
            }
        });

        return root;
    }

    private void uploadProfileImageConfig() {
        this.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select profile image"), 1);
            }
        });
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
        profileImage.setImageBitmap(
                TextUtils.isEmpty(user.getImage())
                        ? BitmapFactory.decodeResource(getResources(), R.drawable.upload_default)
                        : Base64Utils.toImage(user.getImage()));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (data != null) {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContext().getContentResolver(), data.getData());
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        profileImage.setImageBitmap(bitmap);

                        authenticatedUser.setImage(Base64Utils.toImageString(bitmap));
                    }
                }
            } catch (IOException e) {
                Log.e("Profile Image", "Failed to save image", e);
            }
        }
    }


}