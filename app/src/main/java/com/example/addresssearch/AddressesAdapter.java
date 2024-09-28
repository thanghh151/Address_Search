package com.example.addresssearch;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder> {

    private List<String> addresses = new ArrayList<>();
    private String searchQuery = "";

    // Cập nhật dữ liệu và query tìm kiếm
    public void updateResults(List<String> newAddresses, String query) {
        this.addresses = newAddresses;
        this.searchQuery = query.toLowerCase();
        notifyDataSetChanged();
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout tùy chỉnh cho mỗi mục trong RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        String address = addresses.get(position);

        SpannableString spannableString = new SpannableString(address);
        int start = address.toLowerCase().indexOf(searchQuery);
        if (start >= 0) {
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), start, start + searchQuery.length(), 0);
        }
        holder.addressTextView.setText(spannableString);

        holder.iconDirections.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(address)));
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(address)));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;
        ImageView iconLocation;
        ImageView iconDirections;

        AddressViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            iconLocation = itemView.findViewById(R.id.icon_location);
            iconDirections = itemView.findViewById(R.id.icon_directions);
        }
    }
}
