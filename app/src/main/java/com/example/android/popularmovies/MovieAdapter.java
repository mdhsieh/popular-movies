package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<String> mData;

    public MovieAdapter(List<String> data)
    {
        mData = data;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public TextView movieTextView;
        public MovieViewHolder(View itemView)
        {
            super(itemView);
            movieTextView = itemView.findViewById(R.id.tv_movie_name);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recyclerview_row;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        String movie = mData.get(position);
        holder.movieTextView.setText(movie);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public void setMovieData(List<String> movieData) {
        mData = movieData;
        notifyDataSetChanged();
    }
}
