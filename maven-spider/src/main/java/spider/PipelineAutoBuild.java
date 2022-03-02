package spider;

import com.gargoylesoftware.htmlunit.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.security.spec.ECField;
import java.util.HashMap;
import java.util.Map;

public class PipelineAutoBuild implements Runnable{
    static String LOGINURL = "http://192.168.11.237:9091/paas-web/upmsapi/system/login";
    static String COOKIEURL = "http://192.168.11.237:9091/paas-web/runtimeapi/clusterEnv/getClusterByEnvId";
    static String TASKURL = "http://192.168.11.237:9091/paas-web/pipelineapi/v1.8/flow/getFlows";
    static String BUILDURL = "http://192.168.11.237:9091/paas-web/pipelineapi/pipeline/startTasks";

    static HashMap HEADERMAP = new HashMap<String, String>() {{
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
    static String TOKEN;
    static String JSESSIONID;

    WebClient webClient = new WebClient(BrowserVersion.EDGE);

    public void setWebClient() {
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setJavaScriptEnabled(true);
        this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

    public void getToken() throws IOException {
        setWebClient();
        URL tokenUrl = new URL(LOGINURL);
        WebRequest tokenRequest = new WebRequest(tokenUrl, HttpMethod.POST);
        tokenRequest.setAdditionalHeaders(HEADERMAP);
        String tokenBody = "{\n" +
                "  \"userType\":\"0\",\n" +
                "  \"userName\":\"sit-test\",\n" +
                "  \"password\":\"Qm9jbG91ZEAxMjM=\"\n" + "}";
        tokenRequest.setRequestBody(tokenBody);
        var tokenResponse = webClient.loadWebResponse(tokenRequest);
        StringBuffer sb = new StringBuffer();
        String s = tokenResponse.getContentAsString();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'k' && s.charAt(i + 1) == 'e' && s.charAt(i + 2) == 'n') {
                i += 6;
                while (s.charAt(i) != '\"') sb.append(s.charAt(i++));
                break;
            }
        }
        this.TOKEN = sb.toString(); //获取token
    }

    public void getCookie() throws IOException {
        URL ckUrl = new URL(COOKIEURL);
        WebRequest cookieRequest = new WebRequest(ckUrl, HttpMethod.POST);
        String ckBody = "{\n" +
                "    \"envId\": 159\n" +
                "}";
        HEADERMAP.put("envId", "159");
        HEADERMAP.put("token", TOKEN);
        HEADERMAP.put("Accept", "application/json, text/plain, */*");
        HEADERMAP.put("isManager", "false");
        HEADERMAP.put("Content-Type", "application/json;charset=UTF-8");

        cookieRequest.setRequestBody(ckBody);
        cookieRequest.setAdditionalHeaders(HEADERMAP);
        webClient.loadWebResponse(cookieRequest);
        System.out.println("token:"+TOKEN);
        System.out.println("cookie大小:"+webClient.getCookies(ckUrl).size());
        System.out.println(webClient.getCookieManager().getCookies().stream().toList().size());
//        this.JSESSIONID = webClient.getCookies(ckUrl).stream().toList().get(0).getName();
        JSESSIONID = webClient.getCookieManager().getCookies().stream().toList().get(0).getName();
    }

    public Map<String, String> getTaskId() throws IOException {
        URL taskUrl = new URL(TASKURL);
        HEADERMAP.put("Cookie", "JSESSIONID=" + JSESSIONID);
        WebRequest taskRequest = new WebRequest(taskUrl, HttpMethod.POST);
        String taskBody = "{\n" +
                "    \"taskName\": \"\",\n" +
                "    \"envId\": 159,\n" +
                "    \"page\": 1,\n" +
                "    \"rows\": 30\n" +
                "}";
        taskRequest.setRequestBody(taskBody);
        String tasks = webClient.loadWebResponse(taskRequest).getContentAsString();
        TaskJson taskJson = new Gson().fromJson(tasks, TaskJson.class);

        Map taskMap = new HashMap<String, String>();
        taskJson.getRows().stream().forEach(t -> {
            taskMap.put(t.getTaskName(), t.getTaskId().toString());
        });
        return taskMap;
    }

    public void autoBuild(String taskName) throws IOException {

        var sitTaskId = TaskNameMapId.sitTaskNameMap(taskName);
        userBuild(sitTaskId,"sit-test","159");

        var uatTaskId= TaskNameMapId.uatTaskNameMap(taskName);
        userBuild(uatTaskId,"uat-test","160");

        webClient.close();
    }

    public void userBuild(String taskId, String user, String envId) {
        try {
            URL buildUrl = new URL(BUILDURL);
            WebRequest buildRequest = new WebRequest(buildUrl, HttpMethod.POST);

            BuildBody body = new BuildBody(taskId, "0", user,envId);
            String buildBody = new Gson().toJson(body, BuildBody.class);
            buildRequest.setRequestBody(buildBody);
            buildRequest.setAdditionalHeaders(HEADERMAP);

            Thread build = new Thread(() -> {
                try {
                    System.out.println("-----构建------");
                    webClient.loadWebResponse(buildRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            build.start();
            Thread.sleep(3000);
            System.out.println(user+"构建结束");
            build.interrupt();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try{
            wait(3000);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}