import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import spider.Task;
import spider.TaskJson;
import spider.UserBody;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

public class test {

    URI uri = new URI("http://192.168.11.237:9091/paas-web/upmsapi/system/login");
    URL url = new URL("http://192.168.11.237:9091/paas-web/upmsapi/system/login");
    URL cookieUrl = new URL("http://192.168.11.237:9091/paas-web/runtimeapi/clusterEnv/getClusterByEnvId");
    URL taskUrl = new URL("http://192.168.11.237:9091/paas-web/pipelineapi/v1.8/flow/getFlows");
    String user = "sit-user";
    String password = "Qm9jbG91ZEAxMjM=";

    public test() throws URISyntaxException, MalformedURLException {
    }

    static WebClient webClient = new WebClient(BrowserVersion.EDGE);

    @Test
    public void post() throws URISyntaxException, IOException {

        UserBody userBody = new UserBody("0", "sit-test", "Qm9jbG91ZEAxMjM=");
        String body = new Gson().toJson(userBody);
        System.out.println(body);
        var webRequest = new WebRequest(url, HttpMethod.POST);
        webRequest.setRequestBody(body);

        var webClient = new WebClient(BrowserVersion.EDGE);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);

        var map = new HashMap<String, String>() {{
            put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            put("Host", "192.168.11.237:9091");
            put("Connection", "keep-alive");
            put("Content-Type", "application/json");
            put("Referer", "http://192.168.11.237:9091/");
            put("Upgrade-Insecure-Requests", "1");
            put("User-Agent", "mMozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 Edg/98.0.1108.56");
            put("Accept-Encoding", "gzip,deflate");
            put("Accept-Language", "zh-CN,zh;q=0.9");
            put("If-Modified-Since", "Fri, 21 Jun 2019 02:29:27 GMT");
        }};
        webRequest.setAdditionalHeaders(map);
        var webResponse = webClient.loadWebResponse(webRequest);

        String s = webResponse.getContentAsString();
        System.out.println(s);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'k' && s.charAt(i + 1) == 'e' && s.charAt(i + 2) == 'n') {
                i += 6;
                while (s.charAt(i) != '\"') sb.append(s.charAt(i++));
                break;
            }
        }
        s = sb.toString();
        var webCookieRequest = new WebRequest(cookieUrl, HttpMethod.POST);
        String cookBody = "{\n" +
                "    \"envId\": 159\n" +
                "}";
        map.put("envId", "159");
        map.put("token", s);
        map.put("Accept", "application/json, text/plain, */*");
        map.put("isManager", "false");
        map.put("Content-Type", "application/json;charset=UTF-8");
        webCookieRequest.setRequestBody(cookBody);
        webCookieRequest.setAdditionalHeaders(map);
        var cookieRe = webClient.loadWebResponse(webCookieRequest);
        System.out.println(cookieRe.getContentAsString());
        Set cookiesSet = webClient.getCookieManager().getCookies();
        Cookie cookie = (Cookie) cookiesSet.stream().toList().get(0);
        System.out.println(webClient.getCookies(cookieUrl).size());
        System.out.println(cookie.getName());

        map.put("Cookie", "JSESSIONID=" + cookie.getName());
        URL buildUrl = new URL("http://192.168.11.237:9091/paas-web/pipelineapi/pipeline/startTasks");
        WebRequest taskRequest = new WebRequest(buildUrl, HttpMethod.POST);
        taskRequest.setAdditionalHeaders(map);
        String taskBody = "{\n" +
                "    \"taskId\": \"65\",\n" +
                "    \"taskStage\": 0,\n" +
                "    \"imageNamespace\": \"uat-test\",\n" +
                "    \"envId\": 159\n" +
                "}";
        taskRequest.setRequestBody(taskBody);
        Thread t = new Thread(() -> {
            try {
                webClient.loadWebResponse(taskRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        int i = 0;
        while (i < 1000) i++;
        t.interrupt();
        String rows = webClient.loadWebResponse(taskRequest).getContentAsString();
        System.out.println(rows);
//        Gson gson = new Gson();
//        List<Task> taskList = gson.fromJson(rows, TaskJson.class).getRows();
//        HashMap row = new HashMap<String,String>();
//        taskList.stream().forEach(t ->{
//            row.put(t.getTaskName(),t.getTaskId().toString());
//            System.out.println(t.getTaskName());
//        });
//        System.out.println(row.size());
    }

    @Test
    public void taskJson() throws FileNotFoundException {
        JsonReader jr = new JsonReader(new FileReader(".\\ll.json"));
        TaskJson tj = new Gson().fromJson(jr, TaskJson.class);
        var map = new HashMap<String, String>();
        tj.getRows().stream().forEach(i -> {
//            String s = i.getTaskName().substring(4,i.getTaskName().length());
//            map.put(s,i.getTaskName());
            var s = i.getTaskName().split("-", 2);
            if (s.length == 2)
                System.out.println("map.put(\"" + s[1] + "\",\"" + i.getTaskId() + "\");");
        });
    }

    @Test
    public void fileWithNoBlank() throws IOException {
        var uri = "http://192.168.11.166/zentao/user-login.html";
        String taskNum = "49584";
        String projectName = "clm";
        var webclient = new WebClient();
        webclient.getOptions().setCssEnabled(false);
        webclient.getOptions().setJavaScriptEnabled(false);
        HtmlPage htmlPage = webclient.getPage(uri);
        var form = htmlPage.getForms().get(0);

        HtmlTextInput acconut = form.getInputByName("account");
        HtmlPasswordInput password = form.getInputByName("password");
        HtmlButton submit = (HtmlButton) htmlPage.getElementById("submit");

        acconut.setValueAttribute("liushuai");
        password.setValueAttribute("liu342423");
        HtmlPage index = submit.click();
        System.out.println(index.getBody());

        var taskuri = "http://192.168.11.166/zentao/task-view-" + taskNum;
        HtmlPage taskPage = webclient.getPage(taskuri);
        var docu = Jsoup.parse(taskPage.asXml());
        //Element link = docu.getElementById("mainContent").getElementsByTag("a").get(0);
        Element link = docu.selectFirst("ul.files-list")
                .select("a[target]")
                .select("a:not([onclick*=废弃])").get(0);
        System.out.println(link.attr("href"));
        var fileLink = "http://192.168.11.166" + link.attr("href");

        URL url = new URL(fileLink);
        String filename = "D:\\git-dev\\" + projectName + "\\updatelist.txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
             FileWriter writer = new FileWriter(filename);) {

            Deque<String> deque = new ArrayDeque<>();
            String s = reader.readLine();
            if (s != null)
                deque.offer(s);
            while (!deque.isEmpty() || s.length() > 0) {
                if (!deque.isEmpty())
                    writer.append(deque.poll());
                s = reader.readLine();
                if (s != null) {
                    if (s.length() > 0 && !s.matches("\t+")) {
                        deque.offer(s);
                        writer.append("\r\n");
                    }
                } else break;
            }
        }
        webclient.close();
    }
}
