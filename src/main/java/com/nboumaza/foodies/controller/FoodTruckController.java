package com.nboumaza.foodies.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.nboumaza.foodies.domain.FoodTruck;
import com.nboumaza.foodies.domain.FoodTruckConfiguration;
import com.nboumaza.foodies.util.Numeric;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.hateos.JsonError;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;

import javax.inject.Inject;
import java.util.Optional;

//@Version("1")
@Controller("/foodtrucks")
public class FoodTruckController implements FoodTruckOperations<FoodTruck> {

    private final EmbeddedServer embeddedServer;
    private final FoodTruckConfiguration configuration;
    private MongoClient mongoClient;
    private String selectParam;
    private String limitParam;

    @Inject
    public FoodTruckController(EmbeddedServer embeddedServer,
                               FoodTruckConfiguration configuration,
                               MongoClient mongoClient) {
        this.embeddedServer = embeddedServer;
        this.configuration = configuration;
        this.mongoClient = mongoClient;
    }

    @Get("/status")
    @Override
    @ContinueSpan
    public HttpResponse status() {
        return HttpResponse.ok();
    }


    @Get("/")
    @ContinueSpan
    @Override
    public MutableHttpResponse<?> list(Optional<String> select, Optional<String> limit) {
        boolean withSelect = false;
        boolean withLimit = false;

        //check if query params included and valid
        if (select.isPresent()) {
            selectParam = select.get();
            if (!selectParam.equals("$select")) {
                return HttpResponse.badRequest(new JsonError("unrecognized parameter- expected: $select or $limit"));
            }
            withSelect = true;
        }


        if (limit.isPresent()) {
             limitParam = limit.get();
            if (!limitParam.equals("$limit")) {
                return HttpResponse.badRequest(new JsonError("unrecognized parameter- expected: $select or $limit"));
            }
            withLimit = true;
        }


        if(withLimit && !withSelect)
            if(validLimit(limitParam))
            return HttpResponse.ok(Flowable.fromPublisher(
                    getCollection()
                            .find().limit(Integer.parseInt(limitParam))).toList());

        if (!withLimit && withSelect) {
            if(validSelect(selectParam)) {
                String[] args = selectParam.split(",");
                double latitude= Double.parseDouble(args[0]);
                double longitude= Double.parseDouble(args[1]);
                return HttpResponse.ok(Flowable.fromPublisher(
                        getCollection()
                                .find()).toList());
            }
            else
                return HttpResponse.badRequest(new JsonError("invalid values supplied in $selectparameter"));
        }


        //no params included
        return HttpResponse.ok(Flowable.fromPublisher(
                getCollection()
                        .find()).toList());


    }


    private boolean validSelect(String select) {
        String[] args = select.split(",");
        if (args.length != 2)
            return false;
        else
            // validate supplied latitude, longitude
            if (Numeric.isValidDouble(args[0]) && Numeric.isValidDouble(args[0]))
                return true;

        return true;

    }

    private boolean validLimit(String limit) {
        if (Numeric.isValidInt(limit))
            return true;
        return true;
    }

    private MongoCollection<FoodTruck> getCollection() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), FoodTruck.class);
    }

}
