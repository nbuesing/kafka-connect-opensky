package com.github.nbuesing.kafka.connect.opensky.api;

import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class OpenSky {

    private static final String DEFAULT_URL = "https://opensky-network.org/api/";

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

    public OpenSky(final String url, final String username, final String password) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(StringUtils.isNotEmpty(url) ? url : DEFAULT_URL)
                .addConverterFactory(JacksonConverterFactory.create());

        if (username != null) {
            builder.client(new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build()
            );
        }

//        builder.client(new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//
//                        try {
//                            Response response = chain.proceed(chain.request());
//
//                            System.out.println(">>>");
//                            System.out.println(response.body().string());
//                            return response;
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            return null;
//                        }
//                    }
//                })
//                .build());

        retrofit = builder.build();

        api = retrofit.create(RestApi.class);
    }

    public Records getAircrafts(final BoundingBox bb) throws IOException {
        if (bb == null || BoundingBoxUtil.isWorld(bb)) {
            log.info("getAll");
            return api.getAll().execute().body();
        } else {
            log.info("getAll, bb={}", bb);
            return api.getAll(bb.getMinLatitude(), bb.getMaxLatitude(), bb.getMinLongitude(), bb.getMaxLongitude()).execute().body();
        }
    }
}
