package com.radium4ye.webflux;

import org.junit.After;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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

        //window ：将数据按照窗口数进行切割成一个个数据流
        // 和 buff 方法比较类似，只是切割完后的结果，一个是数据流，一个数据组
        //windowWhile  windowUntil windowTimeout 和上面类似
        Flux.range(1, 10).window(3)
                .subscribe(unicastProcessor -> {
                    unicastProcessor.subscribe(obj -> System.out.println("level2-" + obj + "-" + Thread.currentThread().getName()));
                    System.out.println("level1-" + Thread.currentThread().getName());
                    System.out.println("----");
                });

    }

    /**
     * zip方法
     * 这个方法的主要功能就是数据流两两进行压缩，压缩后的默认实现返回Tuple2
     * 当然也可以自定义压缩策略
     */
    @Test
    public void flux_zip() {
        Flux.just("a", "b")
                .zipWith(Flux.just("c", "d"))
                .subscribe(System.out::println);

        Flux.just("a", "b")
                .zipWith(Flux.just("c", "d"), (s1, s2) -> String.format("%s-%s", s1, s2))
                .subscribe(System.out::println);
    }


    @Test
    public void flux_take() {
        Flux<Integer> flux = Flux.range(1, 10);

        //获取数据中开头2个
        flux.take(2).subscribe(System.out::println);

        //结尾2两个
        flux.takeLast(2).subscribe(System.out::println);

        //While 当条件满足时就继续
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);

        //Util 当条件满足是，才将之前的数据交给后续进行处理
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);

    }

    @Test
    public void flux_merge() {

        //合并数据流
//        Flux.merge(Flux.interval(Duration.of(1000, ChronoUnit.MILLIS)), Flux.interval(Duration.of(1000, ChronoUnit.MILLIS)))
//                .subscribe(System.out::println);

        //Sequential 则是将数据流按序进行输出，会先见第一个流中的数据输出，第二个数据流会先放入缓存，等待第一个流。
        Flux.mergeSequential(Flux.interval(Duration.of(1000, ChronoUnit.MILLIS)).take(5), Flux.interval(Duration.of(1000, ChronoUnit.MILLIS)))
                .subscribe(System.out::println);

    }

    /**
     * 异常处理
     */
    @Test
    public void flux_exception_deal() {
        Flux error = Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()));

        System.out.println("--------------1---------------");
        //处理方式1，直接通过订阅方式来分别输出处理
        error.subscribe(System.out::println, System.err::println);

        System.out.println("--------------2---------------");
        //通过 onErrorReturn 将异常信息转换成定义的好的特殊变量
        error.onErrorReturn(0).subscribe(System.out::println);

        System.out.println("--------------3---------------");
        //通过onErrorResume 将异常进行处理，同样返回定义好的特殊值
        error.onErrorResume(e -> {
            if (e instanceof IllegalStateException) {
                return Mono.just(0);
            } else if (e instanceof NullPointerException) {
                return Mono.just(-1);
            }
            return Mono.empty();
        })
                .subscribe(System.out::println);
    }

    /**
     * 异常排查
     * 反应式编程不想命令式编程一样直观，很多问题排查起来比较困难点<br>
     * 故这里引入 checkpoint
     * 可以在异常信息中找到最近的checkpoint，来缩小排查代码范围
     */
    @Test
    public void flux_exception() {
        Flux.just(1, 2, 3, 4, 0, 5, 6)
                .map(x -> 4 / x)
                .checkpoint("test—check-point")
                .subscribe(System.out::println, Throwable::printStackTrace);

    }

    /**
     * 使用 log 方式可以整个流程都以日志的形式打印出来，
     * 可以协助排查问题
     */
    @Test
    public void flux_log() {
        Flux.range(1, 2).log("Range").subscribe(System.out::println);
    }


    /**
     * transform 可以实现先定义好数据流的处理方式（函数）<br/>
     * 不管订阅几次，transform 里面的函数只会执行一次
     */
    @Test
    public void flux_transform() {
        AtomicInteger ai = new AtomicInteger();
        Function<Flux<String>, Flux<String>> filterAndMap = f -> {
            if (ai.incrementAndGet() == 1) {
                return f.filter(color -> !color.equals("orange"))
                        .map(String::toLowerCase);
            } else {
                return f.filter(color -> !color.equals("purple"))
                        .map(String::toUpperCase);
            }
        };

        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .transform(filterAndMap);

        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :" + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: " + d));
    }

    /**
     * compose 用法和 transform 大体一致 <br>
     * 只是 compose 会在每次订阅的时候重新执行一遍函数
     */
    @Test
    public void flux_compose() {
        AtomicInteger ai = new AtomicInteger();
        Function<Flux<String>, Flux<String>> filterAndMap = f -> {
            if (ai.incrementAndGet() == 1) {
                return f.filter(color -> !color.equals("orange"))
                        .map(String::toLowerCase);
            }
            return f.filter(color -> !color.equals("purple"))
                    .map(String::toUpperCase);
        };

        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .compose(filterAndMap);

        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :" + d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: " + d));
    }

    /**
     * 热数据
     */
    @Test
    public void flux_hot_data() throws Exception {
        final UnicastProcessor<String> hotSource = UnicastProcessor.create();

        Flux<String> flux = hotSource.publish()
                .autoConnect()
                .map(String::toUpperCase);

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    hotSource.onNext(i + "");
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        Thread.sleep(2000L);

        flux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: " + d));

        flux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: " + d));

        Thread.sleep(3000L);
        hotSource.cancel();
        hotSource.onNext("cancel_after");

        hotSource.onComplete();

    }

    /**
     * flux 发布订阅线程选择
     *
     * 输出结果 [elastic-2] [single-1] parallel-1
     */
    @Test
    public void flux_thread() {
        Flux.generate(fluxSink -> {
            //parallel-1
            fluxSink.next(Thread.currentThread().getName());
            fluxSink.complete();
        }).publishOn(Schedulers.single())
                //single-1
                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
                .publishOn(Schedulers.elastic())
                //elastic-2
                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
                .subscribeOn(Schedulers.parallel())
                .toStream()
                .forEach(System.out::println);


        //并发消费，这里采用了parallel()，将任务分发到4个不同的线程进行处理
        //注意，如果之后没有调用 runOn() 方法，那么任务会在当前线程分轨处理。
        Flux.range(1, 10)
                .parallel(4)
                .runOn(Schedulers.parallel())
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));
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
