package com.pontes.andre.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.pontes.andre.popularmovies.model.Favorites;
import com.pontes.andre.popularmovies.model.Movie;
import com.pontes.andre.popularmovies.model.OrderEnum;
import com.pontes.andre.popularmovies.net.FetchMovieTask;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityFragment extends Fragment implements ICompletableTask {

    private String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private GridView gridview;

    private Context context;

    private ArrayList<Movie> movies = null;

    private OrderEnum lastOrder;

    private boolean userHasChangedOrder()
    {
        return lastOrder != null && (lastOrder != getOrder());
    }

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.v(LOG_TAG, "On Start()");
        if (userHasChangedOrder())
            updateMovies(getOrder());
        else
            reorderMoviesAndUpdateAdapter(movies);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("movies", movies);
        outState.putSerializable("lastOrder", lastOrder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = inflater.getContext();

        View view =  inflater.inflate(R.layout.fragment_main_activity, container, false);

        gridview = (GridView) view.findViewById(R.id.gridview);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent details = new Intent(getActivity(), DetailActivity.class);

                startActivity(details);
            }
        });

        if (savedInstanceState != null) {
            Log.v(LOG_TAG, "Reading state...");
            movies = savedInstanceState.getParcelableArrayList("movies");

            reorderMoviesAndUpdateAdapter(movies);

            lastOrder = (OrderEnum) savedInstanceState.getSerializable("lastOrder");
            updateAdapter((ArrayList<Movie>) movies);
        } else
            updateMovies(getOrder());

        return view;
    }

    private OrderEnum getOrder()
    {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        String sortBy =  preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));

        switch (sortBy)
        {
            case ("Popularity"):
                return OrderEnum.Popularity;
            case "Highest Rated":
                return OrderEnum.HighestRated;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private boolean getPrefFavoriteFirst()
    {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        Log.v(LOG_TAG, "Show favorites first preference ? " + preferences.getBoolean(getString(R.string.pref_use_favorite_key), false));
        return preferences.getBoolean(getString(R.string.pref_use_favorite_key), false);
    }

    private void updateMovies(OrderEnum order) {

        FetchMovieTask task = new FetchMovieTask(this);
        task.execute(order);

        lastOrder = order;
    }

    public void updateAdapter(final ArrayList<Movie> movies) {
        {
            final Activity context = getActivity();

            if (movies != null) {

                this.movies = movies;

                ImageAdapter imageAdapter = new ImageAdapter(context, movies);

                gridview.setAdapter(imageAdapter);

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        Intent details = new Intent(context, DetailActivity.class);

                        details.putExtra("movie", movies.get(position));

                        context.startActivity(details);
                    }
                });

            } else
                Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTaskCompleted(Object movieArray)
    {
        ArrayList<Movie> movies = (ArrayList<Movie>) movieArray;

        reorderMoviesAndUpdateAdapter(movies);
    }

    private void reorderMoviesAndUpdateAdapter(ArrayList<Movie> movies)
    {
        if (movies == null)
            return;

        if (getPrefFavoriteFirst())
            reorderHavingFavoritesFirst(movies);

        updateAdapter(movies);

    }

    private void reorderHavingFavoritesFirst(ArrayList<Movie> movies) {
        Log.v(LOG_TAG, "Reordering...");

        ArrayList<Long> favorites = Favorites.getInstance().getAll(context);

        HashMap<Long, Movie> hashMovie = new HashMap<Long, Movie>();

        for (Movie m : movies) {
            hashMovie.put(m.getId(), m);
        }

        for (Long f : favorites) {
            if (hashMovie.containsKey(f)) {
                Movie movieToMove = hashMovie.get(f);

                Log.v(LOG_TAG, "Reordering " + movieToMove.getTitle());
                movies.remove(movieToMove);
                movies.add(0, movieToMove);
            }
        }
    }
}
