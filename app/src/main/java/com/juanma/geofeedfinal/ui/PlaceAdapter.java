package com.juanma.geofeedfinal.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.juanma.geofeedfinal.R;
import com.juanma.geofeedfinal.data.Place;

import java.util.ArrayList;
import java.util.List;

// adapter simple:
// - pinta nombre y tipo
// - estrella rellena si es favorito
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.VH> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    private final OnPlaceClickListener listener;
    private final List<Place> items = new ArrayList<>();

    public PlaceAdapter(OnPlaceClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Place> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Place p = items.get(position);

        h.tvName.setText(p.getName());
        h.tvType.setText(p.getType());

        if (p.isFavorite()) {
            h.imgFav.setImageResource(R.drawable.ic_star);
        } else {
            h.imgFav.setImageResource(R.drawable.ic_star_outline);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlaceClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvType;
        ImageView imgFav;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            imgFav = itemView.findViewById(R.id.imgFav);
        }
    }
}
