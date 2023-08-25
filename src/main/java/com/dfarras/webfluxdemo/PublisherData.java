package com.dfarras.webfluxdemo;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Stream;

public class PublisherData {
    public Function<Flux<String>, Flux<String>> applyDelay =
        (element) -> element.delayElements(Duration.ofMillis(100));
    public Function<Flux<String>, Flux<String>> applyLongDelay =
        (element) -> element.delayElements(Duration.ofMillis(190));

    public Function<Flux<String>, Flux<String>> toLowerCase =
        (element) -> element
            .map(String::toLowerCase);

    public Flux<String> threeFlux() {
        return Flux.just("FIRST", "SECOND", "THIRD").log();
    }

    public Flux<String> sixFlux() {
        return Flux.just("FOURTH", "FIFTH", "SIXTH");
    }

    public Flux<Integer> mergedflux() {
        Flux<Integer> initial = Flux.just(1, 2, 3, 4);
        Flux<Integer> merged = Flux.just(5, 6, 7, 8);
        return initial.mergeWith(merged);
    }

    public Flux<String> movieFlux() {
        Stream<String> movie = Stream.of(
            "scene 1",
            "scene 2",
            "scene 3",
            "scene 4",
            "scene 5"
        );
        return Flux.fromStream(movie)
            .transform(this.applyDelay);
    }
}
