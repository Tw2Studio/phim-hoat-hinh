package com.tw2.phimhoathinh.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tw2.phimhoathinh.R;
import com.tw2.phimhoathinh.model.Phim;
import com.tw2.phimhoathinh.view.PhimActivity;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<Phim> data = new ArrayList<>();
    private Context context;

    public MainAdapter(List<Phim> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Phim video = data.get(position);

        holder.tvName.setText(video.getName());

        Picasso.get().load(video.getImage()).into(holder.imageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhimActivity.class);
                intent.putExtra("ID_PLAYLIST", video.getLink());
                intent.putExtra("NAME_PLAYLIST", video.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvName;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_home);
            tvName = (TextView) itemView.findViewById(R.id.tv_home);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_home);
        }
    }
}
