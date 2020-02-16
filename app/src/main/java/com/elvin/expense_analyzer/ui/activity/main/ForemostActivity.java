package com.elvin.expense_analyzer.ui.activity.main;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.elvin.expense_analyzer.R;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.ui.activity.about.AboutActivity;
import com.elvin.expense_analyzer.ui.activity.auth.LoginActivity;
import com.elvin.expense_analyzer.ui.listener.GyroListener;
import com.elvin.expense_analyzer.utils.NotificationChannelUtils;
import com.elvin.expense_analyzer.utils.RetrofitUtils;
import com.elvin.expense_analyzer.utils.SharedPreferencesUtils;
import com.elvin.expense_analyzer.utils.StrictMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ForemostActivity extends AppCompatActivity {

    private UserService userService;
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foremost);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        this.userService = RetrofitUtils.getRetrofit().create(UserService.class);
        this.notificationManagerCompat = NotificationManagerCompat.from(this);
        NotificationChannelUtils channelUtils = new NotificationChannelUtils(this);
        channelUtils.create();
        switchActivityOnMove();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itemLogout:
                logout(false);
                return true;
            case R.id.itemLogoutAll:
                logout(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(boolean logoutAll) {
        String token = "Bearer " + SharedPreferencesUtils.getAuthToken(getApplicationContext());
        Call<Void> call = logoutAll ? userService.logoutAll(token) : userService.logout(token);
        StrictMode.StrictMode();
        try {
            Response<Void> response = call.execute();
            if (!response.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();

            SharedPreferencesUtils.setAuthToken(getApplicationContext(), null);

            notify(
                    NotificationChannelUtils.CHANNEL_1,
                    1,
                    R.drawable.ic_notifications_active_black_24dp,
                    "Logging out?",
                    "Don't forget to track your expense!!!"
            );

            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } catch (IOException e) {
            Log.e("Logout", "Failed to logout", e);
            Toast.makeText(getApplicationContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
        }
    }

    private void notify(String channelId, int notificationId, int icon, String title, String text) {
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManagerCompat.notify(notificationId, notification);
    }

    private void switchActivityOnMove() {
        GyroListener gyroListener = new GyroListener(getApplicationContext());
        gyroListener.setOnGyroChange(new GyroListener.OnGyroChange() {
            @Override
            public void onChange(boolean leftTurn) {
                startActivity(new Intent(ForemostActivity.this, AboutActivity.class));
            }
        });
    }
}
