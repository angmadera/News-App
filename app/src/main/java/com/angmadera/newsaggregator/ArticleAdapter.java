package com.angmadera.newsaggregator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArticleAdapter
        extends RecyclerView.Adapter<ArticleViewHolder> {


    private final MainActivity mainActivity;
    private final ArrayList<Article> articleList;
    private Picasso picasso;

    public ArticleAdapter(MainActivity mainActivity, ArrayList<Article> articleList) {
        this.mainActivity = mainActivity;
        this.articleList = articleList;
        this.picasso = null;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int ViewType) {
        return new ArticleViewHolder(
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.article_entry, parent, false));
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        picasso = Picasso.get();

        Article article = articleList.get(position);

        holder.title.setText(article.getTitle());
        holder.title.setOnClickListener((view) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
            mainActivity.startActivity(intent);
        });

        if (article.getDescription() != null) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(article.getDescription());
            holder.description.setOnClickListener((view) -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                mainActivity.startActivity(intent);
            });
        } else {
            holder.description.setVisibility(View.INVISIBLE);
        }

        if (article.getAuthor() != null) {
            holder.author.setVisibility(View.VISIBLE);
            holder.author.setText(article.getAuthor());
        } else {
            holder.author.setVisibility(View.GONE);
        }

        holder.publishedAt.setVisibility(View.GONE);
        if (article.getPublishedAt() != null) {
            try {
                 Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s").parse(article.getPublishedAt().substring(0, 18));
                assert date != null;
                String formattedDate = new SimpleDateFormat("MMM dd, yyyy HH:mm").format(date);
                 holder.publishedAt.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.publishedAt.setVisibility(View.VISIBLE);
        }

        if (article.getUrlToImage() == null) {
            holder.urlToImage.setImageResource(R.drawable.noimage);
        } else {
            picasso.load(article.getUrlToImage())
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.loading)
                    .into(holder.urlToImage);
            holder.urlToImage.setOnClickListener((view) -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                mainActivity.startActivity(intent);
            });
        }

        holder.count.setText(String.format("%d of %d", article.getCount(), getItemCount()));
    }

    @Override
    public int getItemCount() {return articleList.size();}
}
