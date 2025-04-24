package com.example.android.droidcafeinput;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatusActivity extends AppCompatActivity {
    private TextView statusText, statusDate;
    private ImageView statusIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        statusText = findViewById(R.id.status_text);
        statusDate = findViewById(R.id.status_date);
        statusIcon = findViewById(R.id.status_icon);

        SharedPreferences prefs = getSharedPreferences("order_prefs", MODE_PRIVATE);
        String status = prefs.getString("status", null);
        String date = prefs.getString("date", null);

        if (status == null) {
            statusText.setText("Немає активного замовлення");
            statusDate.setVisibility(View.GONE); // скрываем дату
            statusIcon.setImageResource(R.drawable.ic_status_info);
            statusIcon.setColorFilter(getResources().getColor(R.color.design_default_color_error));
        } else {
            statusText.setText(status);
            statusDate.setText("Оформлено: " + date);
            statusDate.setVisibility(View.VISIBLE);

            if (status.contains("Готується")) {
                statusIcon.setImageResource(R.drawable.ic_status);
                statusIcon.setColorFilter(getResources().getColor(R.color.purple_500));
            } else if (status.contains("Доставлено")) {
                statusIcon.setImageResource(R.drawable.ic_check);
                statusIcon.setColorFilter(getResources().getColor(R.color.teal_700));
            } else if (status.contains("Скасовано")) {
                statusIcon.setImageResource(R.drawable.ic_close);
                statusIcon.setColorFilter(getResources().getColor(R.color.design_default_color_error));
            }
        }
    }
}