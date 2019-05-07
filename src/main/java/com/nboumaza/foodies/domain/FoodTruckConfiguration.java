package com.nboumaza.foodies.domain;

import io.micronaut.context.annotation.ConfigurationProperties;


@ConfigurationProperties("foodtrucks")
public class FoodTruckConfiguration {

    //@Value("${foodtrucks.database.name:foodies}")
    private String databaseName = "foodies";

    //@Value("${foodtrucks.database.name:foodtruck}")
    private String collectionName = "foodtruck";

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
