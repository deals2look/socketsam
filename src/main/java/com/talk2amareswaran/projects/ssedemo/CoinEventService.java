package com.talk2amareswaran.projects.ssedemo;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@Service
public class CoinEventService {

	public Flux<List<AllCoins>> getCoinsEvents(List<AllCoins> coinsList, String country) {

		Flux<Long> interval = Flux.interval(Duration.ofSeconds(10));
		interval.subscribe((i) -> coinsList.forEach(coin -> coin.setPrice_usd(coin.getPrice_usd())));

		Flux<List<AllCoins>> coinTransactionFlux = Flux.fromStream(Stream.generate(() -> coinsList));
		return Flux.zip(interval, coinTransactionFlux).map(Tuple2::getT2);

	}
	
}
