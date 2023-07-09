package com.example.katalogmovie;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katalogmovie.Adapter.MovieAdapter;
import com.example.katalogmovie.DetailActivity;
import com.example.katalogmovie.Model.Response;
import com.example.katalogmovie.Model.Result;
import com.example.katalogmovie.Rest.ApiClient;
import com.example.katalogmovie.Rest.ApiInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private NowPlayingFragment nowPlayingFragment;
    private PopularFragment popularFragment;
    private UpcomingFragment upcomingFragment;
    private MovieAdapter adapter;
    private RecyclerView recyclerView;
    private static final String API_KEY = "d7e3b1ce6f0fd1f9b80a1e013d33b98e";
    private static final String LANGUAGE = "en-US";

    private List<Result> allItems = new ArrayList<>(); // Menyimpan semua item film
    private List<Result> searchResults = new ArrayList<>(); // Menyimpan hasil pencarian

    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        nowPlayingFragment = new NowPlayingFragment();
        popularFragment = new PopularFragment();
        upcomingFragment = new UpcomingFragment();

        setFragment(nowPlayingFragment); // Set default fragment

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_now_playing:
                        setFragment(nowPlayingFragment);
                        return true;
                    case R.id.nav_popular:
                        setFragment(popularFragment);
                        return true;
                    case R.id.nav_upcoming:
                        setFragment(upcomingFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Inisialisasi RecyclerView dan MovieAdapter
        recyclerView = findViewById(R.id.RvMovie);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter(this, new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

        // Panggil setSearchMode(true) pada setiap fragment saat mode pencarian diaktifkan
        if (isSearchMode) {
            if (fragment instanceof NowPlayingFragment) {
                ((NowPlayingFragment) fragment).setSearchMode(true);
            } else if (fragment instanceof PopularFragment) {
                ((PopularFragment) fragment).setSearchMode(true);
            } else if (fragment instanceof UpcomingFragment) {
                ((UpcomingFragment) fragment).setSearchMode(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this); // Set listener untuk search view
        return true; // Return true to indicate that the options menu has been created
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // Tambahkan logika untuk item menu yang dipilih
            isSearchMode = !isSearchMode; // Toggle mode pencarian

            if (isSearchMode) {
                // Jika mode pencarian diaktifkan, sembunyikan fragment
                recyclerView.setVisibility(View.GONE);
                if (nowPlayingFragment != null) {
                    nowPlayingFragment.setSearchMode(true);
                }
                if (popularFragment != null) {
                    popularFragment.setSearchMode(true);
                }
                if (upcomingFragment != null) {
                    upcomingFragment.setSearchMode(true);
                }
            } else {
                // Jika mode pencarian dinonaktifkan, tampilkan kembali fragment
                recyclerView.setVisibility(View.VISIBLE);
                if (nowPlayingFragment != null) {
                    nowPlayingFragment.setSearchMode(false);
                }
                if (popularFragment != null) {
                    popularFragment.setSearchMode(false);
                }
                if (upcomingFragment != null) {
                    upcomingFragment.setSearchMode(false);
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        performSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 1) {
            performSearch(newText);
        } else {
            showAllItems();
        }
        return true;
    }

    private void performSearch(String query) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Response> callSearch = apiInterface.getQuery(API_KEY, LANGUAGE,query, 1);
        callSearch.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    searchResults = response.body().getResults();
                    showSearchResults();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void showSearchResults() {
        recyclerView.setVisibility(View.VISIBLE); // Tampilkan RecyclerView
        adapter.setMovies(searchResults);

        // Sembunyikan daftar film pada NowPlayingFragment, PopularFragment, dan UpcomingFragment
        hideAllFragments();
    }

    private void hideAllFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(nowPlayingFragment);
        fragmentTransaction.hide(popularFragment);
        fragmentTransaction.hide(upcomingFragment);
        fragmentTransaction.commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    private void showAllItems() {
        recyclerView.setVisibility(View.VISIBLE); // Tampilkan RecyclerView
        adapter.setMovies(allItems);

        // Tampilkan kembali daftar film pada NowPlayingFragment, PopularFragment, dan UpcomingFragment
        showFragment(nowPlayingFragment);
        showFragment(popularFragment);
        showFragment(upcomingFragment);
    }

    @Override
    public void onItemClick(Result movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }
}
