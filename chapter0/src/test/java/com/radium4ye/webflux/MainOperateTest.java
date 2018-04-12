package com.radium4ye.webflux;

import org.junit.After;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 关于响应式堆的基本操作
 *
 * @author radium4ye
 */
public class MainOperateTest {


    /**
     * 常见生成冷数据的方法
     */
    @Test
    public void reactor_create_1() {
        //可以将数据放入集合中，然后调用相关方法生成
        Flux<String> flux1 = Flux.just("one", "two", "three");

        Flux<String> flux2 = Flux.fromStream(Stream.of("one", "two", "three"));

        List<String> iterable = Arrays.asList("one", "two", "three");
        Flux<String> flux3 = Flux.fromIterable(iterable);

        Flux<Integer> flux4 = Flux.range(1, 3);

        flux4.subscribe(System.out::println);

        //或者通过 #empty() 生成空数据
        Flux<String> fluxEmpty = Flux.empty();


        //Mono 也是类型
        Mono<String> monoEmpty = Mono.empty();

        Mono<String> mono1 = Mono.just("one");

        //justOrEmpty 可以保证传入参数为空时也不会报错
        Mono<String> mono2 = Mono.justOrEmpty(null);

    }

    /**
     * Flux 更多的生成方法
     */
    @Test
    public void reactor_create_2() {

        // generate 生成，调用 next 即生成数据，complete 则是完成了整个流程
        // 一个循环中只允许调用 next 方式一次
        Flux<Integer> flux1 = Flux.generate(() -> 0, (integer, sink) -> {
            if (++integer > 10) {
                sink.complete();
            }
            sink.next(integer);
            return integer;
        });

        //create 和 generate 恰恰相反 ，需要在一次将所有的数据生成完毕
        Flux<Integer> flux2 = Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        });

        //interval 方法，是按照一定的时间进行数据产生
        Flux<Long> flux3 = Flux.interval(Duration.of(1, ChronoUnit.SECONDS));

//       flux3.subscribe(System.out::println);
    }

    /**
     * buff 相关操作
     */
    @Test
    public void flux_buff() {

        //首先还是先生成数据
        Flux<Long> flux = Flux.interval(Duration.of(1, ChronoUnit.SECONDS));

        //buff：当缓存收到的数据满后，才会进行后续操作
        flux.buffer(3);

        //这个是增加超时时间的方法
        //即达到时间或者缓存收集满后，就会调用后续的方法
        flux.bufferTimeout(3, Duration.of(1500, ChronoUnit.MILLIS));

        //bufferUntil 数据流一直在收集，当达到特定情况后（e.g. 收集到偶数），把数据一起给后续执行
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0)
                .subscribe(obj -> System.out.println("bufferUntil:" + obj));

        //bufferWhile 数据流一直在过滤，数据出现了特定情况后（e.g. 收集到偶数），进行收集，并紧接着给后续执行
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0)
                .subscribe(obj -> System.out.println("bufferWhile:" + obj));

    }

    /**
     * 由于一些测试用例不窃取主线程，就会导致主线程直接走完后，
     * 整个程序就终止了，无法输出预期效果
     *
     * @throws InterruptedException
     */
    @After
    public void after() throws InterruptedException {
        Thread.sleep(10000L);
    }
}
