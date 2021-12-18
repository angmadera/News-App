package com.angmadera.newsaggregator;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView author;
    TextView description;
    ImageView urlToImage;
    TextView publishedAt;
    TextView count;

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.articleTitle);
        author = itemView.findViewById(R.id.articleAuthors);
        description = itemView.findViewById(R.id.description);
        urlToImage = itemView.findViewById(R.id.articleImage);
        publishedAt = itemView.findViewById(R.id.dateTime);
        count = itemView.findViewById(R.id.count);
        description.setMovementMethod(new ScrollingMovementMethod());
    }
}
