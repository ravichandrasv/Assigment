package accionlabs.com.quote.network;

import java.util.ArrayList;

import accionlabs.com.quote.model.QuotesResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {



    @GET("/qod")
    Call<QuotesResponse> getTopRatedMovies();

    //recent
    @GET("/qod")
    Call<QuotesResponse> getTopRatedMoviesDetail();
}
