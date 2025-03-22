package com.example.stars.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.stars.R;
import com.example.stars.beans.Star;
import com.example.stars.service.StarService;

import java.util.ArrayList;
import java.util.List;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarViewHolder> implements Filterable {

    private List<Star> stars;
    private List<Star> starsFilter;
    private Context context;
    private NewFilter mfilter;

    public StarAdapter(Context context, List<Star> stars) {
        this.context = context;
        this.stars = stars != null ? stars : new ArrayList<>();
        this.starsFilter = new ArrayList<>(this.stars);
        this.mfilter = new NewFilter(this);
    }

    @NonNull
    @Override
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.star_item, viewGroup, false);
        final StarViewHolder holder = new StarViewHolder(v);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Star star = starsFilter.get(position);

                View popup = LayoutInflater.from(context).inflate(R.layout.star_edit_item, null, false);
                final ImageView img = popup.findViewById(R.id.img);
                final RatingBar bar = popup.findViewById(R.id.ratingBar);
                final TextView idss = popup.findViewById(R.id.idss);

                Glide.with(context)
                        .load(star.getImg())
                        .apply(new RequestOptions().override(100, 100))
                        .into(img);

                bar.setRating(star.getStar());
                idss.setText(String.valueOf(star.getId()));

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Notez : ")
                        .setMessage("Donner une note entre 1 et 5 :")
                        .setView(popup)
                        .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                float s = bar.getRating();
                                int ids = Integer.parseInt(idss.getText().toString());
                                Star updatedStar = StarService.getInstance().findById(ids);
                                updatedStar.setStar(s);
                                StarService.getInstance().update(updatedStar);
                                notifyItemChanged(position);
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .create();
                dialog.show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Etes vous sure de vouloir suprimer cette item?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = holder.getAdapterPosition();
                        Star star = starsFilter.get(position);
                        StarService.getInstance().delete(star);
                        starsFilter.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, starsFilter.size());
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StarViewHolder starViewHolder, int i) {
        Star star = starsFilter.get(i);
        Glide.with(context)
                .load(star.getImg())
                .apply(new RequestOptions().override(100, 100))
                .into(starViewHolder.img);
        starViewHolder.name.setText(star.getName().toUpperCase());
        starViewHolder.stars.setRating(star.getStar());
        starViewHolder.idss.setText(String.valueOf(star.getId()));
    }

    @Override
    public int getItemCount() {
        return starsFilter != null ? starsFilter.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return mfilter;
    }

    public class StarViewHolder extends RecyclerView.ViewHolder {
        TextView idss;
        ImageView img;
        TextView name;
        RatingBar stars;
        RelativeLayout parent;

        public StarViewHolder(@NonNull View itemView) {
            super(itemView);
            idss = itemView.findViewById(R.id.ids);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            stars = itemView.findViewById(R.id.stars);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    public class NewFilter extends Filter {
        private RecyclerView.Adapter adapter;

        public NewFilter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Star> filteredList = new ArrayList<>();
            final FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(stars);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Star star : stars) {
                    if (star.getName().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(star);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            starsFilter.clear();
            if (filterResults.values != null) {
                starsFilter.addAll((List<Star>) filterResults.values);
            }
            adapter.notifyDataSetChanged();
        }
    }
}