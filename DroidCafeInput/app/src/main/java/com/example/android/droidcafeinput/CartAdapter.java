package com.example.android.droidcafeinput;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> items;

    private Runnable onCartChanged;

    public CartAdapter(List<Product> items) {
        this.items = items;
        this.onCartChanged = onCartChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = items.get(position);
        holder.name.setText(product.getName());
        holder.description.setText(product.getDescription());
        holder.quantity.setText("x" + product.getCount());
        holder.image.setImageResource(product.getImageResId());

        holder.delete.setOnClickListener(v -> {
            if (product.getCount() > 1) {
                product.decrementCount();
            } else {
                items.remove(position);
            }
            notifyDataSetChanged();
            onCartChanged.run();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, quantity;
        ImageView image;
        ImageButton delete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            description = itemView.findViewById(R.id.product_description);
            quantity = itemView.findViewById(R.id.product_quantity);
            image = itemView.findViewById(R.id.product_image);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }
}
