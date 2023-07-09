package com.example.katalogmovie;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.katalogmovie.Model.Result;
import com.example.katalogmovie.R;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    String title, overview, image;
    double rating;
    ImageView imgDetail;
    TextView tvTitle, tvDetail, tvRating;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvjudul);
        tvDetail = findViewById(R.id.tvdes);
        imgDetail = findViewById(R.id.imgMovie);
        tvRating = findViewById(R.id.tvrat);

        result = getIntent().getParcelableExtra(EXTRA_MOVIE);

        title = result.getOriginalTitle();
        overview = result.getOverview();
        image = result.getPosterPath();
        rating = result.getVoteAverage();

        tvTitle.setText(title);
        tvDetail.setText(overview);

        // Format nilai rating menjadi "0.0"
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String ratingText = decimalFormat.format(rating);
        tvRating.setText(ratingText);

        Glide.with(getApplicationContext())
                .load("https://image.tmdb.org/t/p/w500/" + image)
                .into(imgDetail);
    }
}