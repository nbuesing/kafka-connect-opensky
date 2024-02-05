package com.github.nbuesing.kafka.connect.opensky.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nbuesing.kafka.connect.opensky.util.BoundingBoxUtil;
import feign.Feign;
import feign.Param;
import feign.Request;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;

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

  private RestApi feign;

  public interface RestApi {
    @RequestLine("GET /states/all")
    Records getAll();

    @RequestLine("GET /states/all?lamin={lamin}&lamax={lamax}&lomin={lomin}&lomax={lomax}")
    Records getAll(
            @Param("lamin") Double minLatitude,
            @Param("lamax") Double maxLatitude,
            @Param("lomin") Double minLongitude,
            @Param("lomax") Double maxLongitude
    );
  }

  public OpenSky(
          final String url,
          final String username,
          final String password,
          final Optional<Duration> callTimeout,
          final Optional<Duration> connectTimeout,
          final Optional<Duration> readTimeout
  ) {

//            .client(create(username, password, callTimeout, connectTimeout, readTimeout))

    final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    final Feign.Builder builder = Feign.builder()
            .encoder(new JacksonEncoder(objectMapper))
            .decoder(new JacksonDecoder(objectMapper));

    if (username != null && password != null) {
      builder.requestInterceptor(new BasicAuthRequestInterceptor(username, password));
    }

    if (connectTimeout.isPresent() && readTimeout.isPresent()) {
      builder.options(new Request.Options(connectTimeout.get(), readTimeout.get(), true));
    }

    feign = builder.target(RestApi.class, StringUtils.isNotEmpty(url) ? url : DEFAULT_URL);

  }

  public Records getAircrafts(final BoundingBox bb) throws IOException {
    if (bb == null || BoundingBoxUtil.isWorld(bb)) {
      log.info("getAll");
      return feign.getAll();
    } else {
      log.info("getAll, bb={}", bb);
      return feign.getAll(bb.getMinLatitude(), bb.getMaxLatitude(), bb.getMinLongitude(), bb.getMaxLongitude());
    }
  }
}
