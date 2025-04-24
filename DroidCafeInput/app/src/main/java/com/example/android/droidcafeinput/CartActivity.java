package com.example.android.droidcafeinput;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartAdapter(Cart.getItems());
        recyclerView.setAdapter(adapter);

        adapter = new CartAdapter(Cart.getItems());
        recyclerView.setAdapter(adapter);
        updateTotalPrice();

        Button checkoutBtn = findViewById(R.id.checkout_button);
        checkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderActivity.class); // Экран оформления
            startActivity(intent);
        });
    }

    private void updateTotalPrice() {
        double total = 0.0;
        for (Product p : Cart.getItems()) {
            total += p.getPrice() * p.getCount();
        }

        TextView totalPriceView = findViewById(R.id.total_price);
        totalPriceView.setText(String.format("Итого: %.2f₴", total));
    }


}