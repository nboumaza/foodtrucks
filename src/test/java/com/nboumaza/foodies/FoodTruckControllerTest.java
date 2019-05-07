package com.nboumaza.foodies;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.nboumaza.foodies.domain.FoodTruck;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.runtime.server.EmbeddedServer;
import org.bson.Document;
import org.junit.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import io.micronaut.runtime.server.EmbeddedServer;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FoodTruckControllerTest {


    private ApplicationContext context;
    private static MongodExecutable mongodExecutable = null;
    private static MongoClient mongoClient;
    private MongoCollection<Document> col;

    @Inject
    FoodTruckControllerTestClient controller;

    @Before
    public void setup() throws IOException {

        MongodStarter starter = MongodStarter.getDefaultInstance();

        context =  ApplicationContext.run(Environment.TEST);

        String bindIp = "localhost";
        int port = 12345;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build();


            mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();
            mongoClient = new MongoClient(bindIp, port);

    }

    @After
    public  void cleanup() {
        mongoClient.getDatabase("foodies").drop();
        if (mongodExecutable != null)
               mongodExecutable.stop();
    }


    //@Test
    public void testListFoodTruck() {
        FoodTruckControllerTestClient client = context.getBean(FoodTruckControllerTestClient.class);
        FoodTruck ft = client.save(new FoodTruck(123, "a", 0, 0, "abc"));
        assertNotNull(ft);

        HttpRequest<?> request = HttpRequest.GET("");

        MutableHttpResponse<?> resp = client.list(Optional.empty(), Optional.empty());


        assertEquals(resp.body(), 1);

    }

}
