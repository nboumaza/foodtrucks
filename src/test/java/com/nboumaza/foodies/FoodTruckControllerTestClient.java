package com.nboumaza.foodies;

import com.nboumaza.foodies.controller.FoodTruckOperations;
import com.nboumaza.foodies.domain.FoodTruck;
import io.micronaut.http.client.annotation.Client;

@Client("/foodtrucks")
interface FoodTruckControllerTestClient extends FoodTruckOperations<FoodTruck> {

    FoodTruck save(FoodTruck foodtruck);

}
