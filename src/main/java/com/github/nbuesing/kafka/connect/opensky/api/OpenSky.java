package com.github.nbuesing.kafka.connect.opensky.api;

import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;

/**
 * https://opensky-network.org/apidoc/rest.html
 * <p>
 * Anonymous Users
 * <p>
 * time parameter is always ignored
 * data resolution of 10 seconds time = (now - (now % 10))
 * <p>
 * Authenticated Users
 * <p>
 * time parameter can to 1 hour in the past, time < now - 3600 results in 400
 * data resolution of 5 seconds time = (now - (now % 5))
 */
public class OpenSky {

    private static final String HOST = "https://opensky-network.org/api/";

    private Retrofit retrofit;

    private RestApi api;

    public interface RestApi {

        @GET("states/all")
        Call<Records> getAll();

        // time
        @GET("states/all")
        Call<Records> getAll(
                @Query("lamin") Double minLatitude,
                @Query("lamax") Double maxLatitude,
                @Query("lomin") Double minLongitude,
                @Query("lomax") Double maxLongitude
        );

    }

    public OpenSky(final String username, final String password) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(JacksonConverterFactory.create());

        if (username != null) {
            builder.client(new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build()
            );
        }

        retrofit = builder.build();

        api = retrofit.create(RestApi.class);
    }

    public Records getAircrafts(final BoundingBox bb) throws IOException {
        if (bb == null || BoundingBoxUtil.isWorld(bb)) {
            return api.getAll().execute().body();
        } else {
            return api.getAll(bb.getMinLatitude(), bb.getMaxLatitude(), bb.getMinLongitude(), bb.getMaxLongitude()).execute().body();
        }
    }
}
