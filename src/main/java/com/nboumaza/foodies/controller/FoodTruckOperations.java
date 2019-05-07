package com.nboumaza.foodies.controller;

import com.nboumaza.foodies.domain.FoodTruck;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;

import java.util.Optional;

public interface FoodTruckOperations<T extends FoodTruck> {


    /**
     * @return ok (200) if healthy
     */
    @Get("/status")
    HttpResponse status();

    /**
     * $select parameter type format : [long,lat]
     *
     * @return all food trucks {for clients that wish to draw an initial map with all }
     */
    @Get("/")
    MutableHttpResponse<?> list(@QueryValue("$select") Optional<String> select, @QueryValue("$limit") Optional<String> limit);


}
