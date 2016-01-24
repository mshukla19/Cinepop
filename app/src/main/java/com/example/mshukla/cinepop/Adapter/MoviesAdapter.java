package com.example.mshukla.cinepop.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mshukla.cinepop.Util.Constants;
import com.example.mshukla.cinepop.Model.Movie;
import com.example.mshukla.cinepop.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by manas on 12/17/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Movie> mDataset;
    private Context mContext;
    private int mPosterWidth;
    private int mPosterHeight;
    private ViewHolder.ClickListener mOnItemClickListener;
    public MoviesAdapter(Context context, List<Movie> dataset, int actualWidth, ViewHolder.ClickListener OnItemClickListener) {
        super();
        mContext = context;
        mDataset = dataset;
        mPosterWidth = actualWidth;
        mPosterHeight = (int) (mPosterWidth / Constants.TMDB_RESIZE_RATIO);
        mOnItemClickListener = OnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,parent,false);
        ViewHolder item = new ViewHolder(view, mOnItemClickListener);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        lp.width = mPosterWidth;
        lp.height = mPosterHeight;
        view.setLayoutParams(lp);
        return item;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.attachMovie(mDataset.get(position));
        String url = Constants.IMAGE_URL + holder.movie.getPosterPath();
        Glide.with(mContext)
                .load(url)
                .crossFade()
                .placeholder(R.drawable.movie_placeholder)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.movie_image)
        ImageView mImageView;
        private final ClickListener mOnclickListener;

        private Movie movie;

        public ViewHolder(View itemView, ClickListener OnclickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            mOnclickListener = OnclickListener;
            ButterKnife.bind(this, itemView);
        }

        public void attachMovie(Movie movie) {
            this.movie = movie;
        }


        @Override
        public void onClick(View view) {
            mOnclickListener.moviePosterClick(itemView, movie);
        }

        public interface ClickListener {
            void moviePosterClick(View itemView, Movie movie);
        }
    }
}
