package spider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Spider {
    public static String YIYUANPREFIX = "http://www.layy.cn:8000/uploadfiles/hr/202206";
    public static List<String> url = new LinkedList();
    private static Duration duration;
    public static ExecutorService service = new ThreadPoolExecutor(10,
            200,
            0L,
            TimeUnit.MILLISECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
            );
    public static void main(String[] args) throws InterruptedException {
        var day = args[0];
        var hour = args[1];
        YIYUANPREFIX = YIYUANPREFIX+day+hour;
        System.out.println(YIYUANPREFIX);
        LocalTime past = LocalTime.now();
        Thread t = new Thread(()->{
            int minute = 0, second = 0;
            while (minute < 60) {
                var pre = YIYUANPREFIX + String.format("%02d", minute);
                while (second < 60) {
                    service.submit(new MilliSecond(pre, second));
                    second = second + 1;
                }
                second = 0;
                minute = minute + 1;
            }
        },"ThreadPool");
        t.start();
        t.join();
        service.shutdownNow();
        while (!service.isTerminated()) Thread.sleep(10000);
        check();
        duration = Duration.between(past,LocalTime.now());
        writeFile();
//        webflux();
    }
    public static void check(){
        WebClient webClient = new WebClient();
        url = url.parallelStream().filter(s-> {
            try {
                return webClient.loadWebResponse(new WebRequest(new URL(s))).getStatusCode()==200;
            } catch (IOException e) {
                System.out.println(e);
                return false;
            }
        }).collect(Collectors.toList());
    }
    public static void writeFile(){
//        File file = new File("C:\Users\root\Desktop\log.txt");
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/home/ubuntu/log"+".txt"))){
            for(String s : url){
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
            bufferedWriter.write(duration.toHoursPart()+"小时"+duration.toMinutesPart()+"分钟");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    public static void webflux(){
//        var webClient =  org.springframework.web.reactive.function.client.WebClient.create(Spider.YIYUANPREFIX);
//        Mono<String> res = webClient.get().uri("095012111.jpg")
//                .retrieve()
//                .bodyToMono(String.class)
//                .timeout(Duration.ofSeconds(2))
//                .onErrorReturn("???");
//        System.out.println(res.block());
//    }
}
