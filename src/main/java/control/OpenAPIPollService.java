package control;

import client.OpenApiClient;
import configuration.OpenAPIServicesConfig;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class OpenAPIPollService {

    private static Logger log = LoggerFactory.getLogger(OpenAPIPollService.class);

    @Inject
    OpenAPIServicesConfig openAPIServicesConfig;

    @Inject
    OpenAPIService openAPIService;

    @Inject
    @ConfigProperty(name = "polling.frequency", defaultValue = "10")
    long frequencyPolling;
    
    @Inject
    @ConfigProperty(name = "polling.initialDelay", defaultValue = "1")
    long initialDelay;

    @Inject
    @ConfigProperty(name = "polling.unit", defaultValue = "SECONDS")
    String pollUnit;

    void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting...");

        Multi.createFrom().ticks().startingAfter(Duration.of(initialDelay, ChronoUnit.valueOf(pollUnit)))
                .every(Duration.of(frequencyPolling, ChronoUnit.valueOf(pollUnit)))
                .onItem().invoke(() -> Multi.createFrom().iterable(openAPIServicesConfig.configs())
                .onItem().transform(openAPIConfiguration -> {
                    try {
                        return Tuple2.of(openAPIConfiguration,
                                RestClientBuilder.newBuilder()
                                        .baseUrl(new URL(openAPIConfiguration.url()))
                                        .build(OpenApiClient.class)
                                        .getOpenAPI());

                    } catch (MalformedURLException e) {
                        log.error("Unable to create url");

                    }
                    return null;
                }).collect().asList().toMulti().onItem().transformToMultiAndConcatenate(unis -> {

                    AtomicInteger callbackCount = new AtomicInteger();
                    return Multi.createFrom().emitter(emitter -> Multi.createFrom().iterable(unis)
                            .subscribe().with(uniTuple ->
                                    uniTuple.getItem2().subscribe().with(openapiResponse -> {
                                        emitter.emit(callbackCount.incrementAndGet());
                                        openAPIService.addOpenAPI(uniTuple.getItem1(), openapiResponse);

                                        if (callbackCount.get() == unis.size()) {
                                            emitter.complete();
                                        }
                                    })
                            ));

                }).subscribe().with(s -> {
                        },
                        Throwable::printStackTrace, () -> openAPIService.merge()))
                .subscribe().with(aLong -> log.info("Tic Tac with iteration: " + aLong));


    }
}