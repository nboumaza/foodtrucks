package com.nboumaza.foodies.init;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.nboumaza.foodies.domain.FoodTruck;
import com.nboumaza.foodies.domain.FoodTruckConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.reactivex.Single;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Upon application startup fetch foodtrucks data and load to mongo
 */
//@Requires(notEnv = Environment.TEST) //Don't load data in unit tests.
@Singleton
public class BootStrap {

    //TODO add a $where to exclude empty or "0" for long lat
    @Client("https://data.sfgov.org/resource/rqzj-sfat.json?" +
            "$select=applicant,longitude,latitude,fooditems")
    @Inject
    private RxHttpClient dataClient;

    private final EmbeddedServer embeddedServer;
    private MongoClient mongoClient;

    //private final BeerMarkupConfiguration configuration;

    private ArrayList<FoodTruck> ftData;

    private final FoodTruckConfiguration configuration;

    private final static Logger log = LoggerFactory.getLogger(BootStrap.class);

    @Inject
    public BootStrap(EmbeddedServer embeddedServer, MongoClient mongoClient, FoodTruckConfiguration configuration) {
        this.embeddedServer = embeddedServer;
        this.mongoClient = mongoClient;
        this.configuration = configuration;

    }


    @EventListener
    public void onStartup(ServerStartupEvent event) {

        loadFoodTruckData();
    }


    /**
     * Acts as a client to sfgov data source provider to fetch
     * foodtrucks data by criteria
     */
    private void loadFoodTruckData() {

        //fetch data
        HttpRequest<?> request = HttpRequest.GET("");
        HttpResponse<List> rsp = dataClient.toBlocking().exchange(request, Argument.of(List.class, FoodTruck.class));
        ftData = (ArrayList<FoodTruck>) rsp.body();

        log.info(">>>>>>>>>> FoodTrucks records loaded:" + ftData.size());

        //load data
        Single.fromPublisher(bulkWriteDocument(ftData))
                .subscribe(s -> log.info(">>>>>>>> Bulk write Status: " + s));

        //TODO
        //build index on locationid / foodies / long / lat

    }

    /**
     * Bulk insert foodtrucks data in mongodb
     *
     * @param foodtrucks list of foodtrucks to insert
     * @return BulkWriteResult bulk write result publisher
     */
    private Publisher<BulkWriteResult> bulkWriteDocument(ArrayList<FoodTruck> foodtrucks) {


        List<WriteModel<FoodTruck>> writes = new ArrayList<>(foodtrucks.size());
        Publisher<BulkWriteResult> bulkWriteResult = null;

        try {
            foodtrucks.forEach(truck -> {
                InsertOneModel<FoodTruck> insert = new InsertOneModel<>(truck);
                writes.add(insert);
            });

            // no need for sequencial write in this case.
            // mongo will chunk data and perform parallel inserts

            BulkWriteOptions bwo = new BulkWriteOptions().
                    ordered(false).
                    bypassDocumentValidation(false);

            bulkWriteResult = getCollection().bulkWrite(writes, bwo);

        } catch (Exception e) {
            log.error("Exception ocured during bulk write " + e.getMessage());
        }
        //log.info("int rowsInserted= "+bulkWriteResult.getModifiedCount());

        return bulkWriteResult;

    }


    private MongoCollection<FoodTruck> getCollection() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), FoodTruck.class);
    }


}
