package com.example.katalogmovie;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katalogmovie.Adapter.MovieAdapter;
import com.example.katalogmovie.Model.Response;
import com.example.katalogmovie.Model.Result;
import com.example.katalogmovie.Rest.ApiClient;
import com.example.katalogmovie.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PopularFragment extends Fragment implements MovieAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private TextView textViewTitle;

    private static final String API_KEY = "d7e3b1ce6f0fd1f9b80a1e013d33b98e";
    private static final String LANGUAGE = "en-US";
    private static final String CATEGORY = "popular";
    private static final int PAGE = 1;
    private List<Result> allItems = new ArrayList<>();

    private boolean isSearchMode = false;

    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular, container, false);
        textViewTitle = view.findViewById(R.id.Popular);
        recyclerView = view.findViewById(R.id.RvMovie);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MovieAdapter(getActivity(), new ArrayList<Result>(), this);
        recyclerView.setAdapter(adapter);

        // Load Popular movies
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Response> call = apiInterface.getMovie(CATEGORY, API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    List<Result> movies = response.body().getResults();
                    adapter.setMovies(movies);
                    textViewTitle.setText("Popular");
                    allItems = new ArrayList<>(movies); // Simpan daftar film awal

                    // Tambahkan logika untuk mengatur visibilitas RecyclerView berdasarkan isSearchMode
                    if (isSearchMode) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                // Handle failure
            }
        });

        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onItemClick(Result movie) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    public void setSearchMode(boolean isSearchMode) {
        this.isSearchMode = isSearchMode;

        // Tambahkan logika untuk mengatur visibilitas RecyclerView berdasarkan isSearchMode
        if (recyclerView != null) {
            if (isSearchMode) {
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
