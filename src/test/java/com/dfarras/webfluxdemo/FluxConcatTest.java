package com.dfarras.webfluxdemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;
import java.util.stream.Stream;

public class FluxConcatTest {
    private final PublisherData data = new PublisherData();

    @Test
    public void concat() {
        Flux.concat(data.threeFlux().transform(data.applyDelay), data.sixFlux().transform(data.applyLongDelay))
            .transform(data.applyDelay)
            .timed()
            .doOnNext(str -> System.out.println("value: " + str.get() + " - delay: " + str.elapsed().toMillis()))
            .blockLast();
    }

    @Test
    public void merge() {
        Flux.merge(data.threeFlux().transform(data.applyDelay), data.sixFlux().transform(data.applyLongDelay))
            .timed()
            .doOnNext(str -> System.out.println("value: " + str.get() + " - delay: " + str.elapsed().toMillis()))
            .blockLast();
    }

    @Test
    public void mergeSequential() {
        Flux.mergeSequential(data.threeFlux(), data.sixFlux())
            .transform(data.applyDelay)
            .timed()
            .doOnNext(str -> System.out.println("value: " + str.get() + " - delay: " + str.elapsed().toMillis()))
            .blockLast();
    }

    @Test
    public void verify() {
        StepVerifier.create(this.data.threeFlux())
            .expectNext("FIRST", "SECOND", "THIRD")
            .verifyComplete();
    }

    @Test
    public void expect() {
        //Add verify
        StepVerifier.create(this.data.threeFlux())
            .expectNext("FIRST", "SECOND", "THIRD")
            .expectComplete();
    }

    @Test
    public void mergeVerify() {
        TestPublisher.<String>create().next("THIS", "THAT")
            .assertSubscribers(1);
        StepVerifier.create(this.data.mergedflux())
            .expectFusion();
    }

    private Stream<String> getMovie() {
        System.out.println("Got the movie streaming request");
        return Stream.of(
            "scene 1",
            "scene 2",
            "scene 3",
            "scene 4",
            "scene 5"
        );
    }

    @Test
    public void coldPublisher() throws InterruptedException {
        Flux<String> netFlux = Flux.fromStream(() -> getMovie())
            .delayElements(Duration.ofSeconds(1));

        netFlux.subscribe(scene -> System.out.println("First subscriber " + scene));

        Thread.sleep(3000);
        netFlux.subscribe(scene -> System.out.println("Second subscriber " + scene));
        Thread.sleep(5000);
    }

    //If the flux is finished share will make it repeat
    @Test
    public void hotSharedPublisher() throws InterruptedException {
        Flux<String> movieTheatre = Flux.fromStream(() -> getMovie())
            .delayElements(Duration.ofSeconds(1)).share();

        movieTheatre.subscribe(scene -> System.out.println("First subscriber " + scene));

        Thread.sleep(3000);
        movieTheatre.subscribe(scene -> System.out.println("Second subscriber " + scene));
        Thread.sleep(5000);
    }

    @Test
    public void hotCachedPublisher() throws InterruptedException {
        Flux<String> movieTheatre = Flux.fromStream(() -> getMovie())
            .delayElements(Duration.ofSeconds(1)).cache();

        movieTheatre.subscribe(scene -> System.out.println("First subscriber " + scene));

        Thread.sleep(3000);
        movieTheatre.subscribe(scene -> System.out.println("Second subscriber " + scene));
        Thread.sleep(5000);
    }


}
