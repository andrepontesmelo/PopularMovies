package com.pontes.andre.popularmovies;

import java.util.ArrayList;

public class MoviesJsonParser {
    public String getImageUrl (int position)
    {
        switch (position) {
            case 0:
                return "http://image.tmdb.org/t/p/w500/ktyVmIqfoaJ8w0gDSZyjhhOPpD6.jpg";
            case 1:
                return "http://image.tmdb.org/t/p/w500/bHarw8xrmQeqf3t8HpuMY7zoK4x.jpg";

            case 2:
                return "http://image.tmdb.org/t/p/w500/9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg";

            case 3:
                return "http://image.tmdb.org/t/p/w500/saF3HtAduvrP9ytXDxSnQJP3oqx.jpg";

            case 4:
                return "http://image.tmdb.org/t/p/w500/yUlpRbbrac0GTNHZ1l20IHEcWAN.jpg";

            default:
                return "http://image.tmdb.org/t/p/w500/aqNJrAxudMRNo8jg3HOUQqdl2xr.jpg";

        }
    }

    public ArrayList<Movie> Parse(String json)
    {
        ArrayList<Movie> result = new ArrayList<Movie>();

        result.add(new Movie());
        result.add(new Movie());

        return result;
    }
}
