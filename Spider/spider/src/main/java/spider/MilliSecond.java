package spider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class MilliSecond implements Runnable {
    int second;
    String pre;
    public MilliSecond(String pre, int second) {
        this.pre = pre;
        this.second = second;
    }

    @Override
    public void run() {
        Random r = new Random();
        try (WebClient wc = new WebClient();){
            var s = this.pre + String.format("%02d", second);
            for (int i = 0; i < 1000; i++) {
                var fullUrl = s + String.format("%03d", i) + ".jpg";
                var wr = new WebRequest(new URL(fullUrl));
                if (wc.loadWebResponse(wr).getStatusCode() == 200) {
                    Spider.url.add(fullUrl);
                    Thread.sleep(3000);
                }
                if(i % r.nextInt(3,21) == 0) Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
