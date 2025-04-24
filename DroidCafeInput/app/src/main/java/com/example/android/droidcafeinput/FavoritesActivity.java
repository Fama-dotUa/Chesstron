package com.example.android.droidcafeinput;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {
    private LinearLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        container = findViewById(R.id.favorites_container);
        Set<String> favorites = FavoritesManager.getFavorites(this);

        if (favorites.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("У вас поки немає обраних товарів.");
            empty.setTextSize(16f);
            container.addView(empty);
        } else {
            for (String item : favorites) {
                TextView tv = new TextView(this);
                tv.setText("❤️ " + item);
                tv.setTextSize(18f);
                tv.setPadding(0, 8, 0, 8);
                container.addView(tv);
            }
        }
    }
}