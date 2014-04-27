package com.nurkiewicz.java8;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Awaitility.to;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class MultiRunnerTest {

	@Test
	public void shouldExecuteTasksInMultipleThreads() throws Exception {
		//given
		final Map<Long, String> threads = new ConcurrentHashMap<>();

		//when
		IntStream.range(0, 100).forEach(i ->
						MultiRunner.runMultiThreaded(() -> {
							final Thread thread = Thread.currentThread();
							threads.put(thread.getId(), thread.getName());
						})
		);

		//then
		await().untilCall(to(threads).size(), is(MultiRunner.THREAD_COUNT));
		assertThat(threads).doesNotContainKey(Thread.currentThread().getId());
	}

	@Test
	public void shouldRunMultipleBlocks() throws Exception {
		//given
		final LongAdder counter = new LongAdder();

		//when
		MultiRunner.runMultiThreaded(Arrays.asList(
				() -> counter.add(1),
				() -> counter.add(2),
				() -> counter.add(3)
		));

		//then
		await().untilCall(to(counter).sum(), is(1 + 2 + 3));
	}

}