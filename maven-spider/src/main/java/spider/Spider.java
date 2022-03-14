package spider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Spider {
    static String uri = "http://192.168.11.166/zentao/user-login.html";
    static String xml = "D:\\git-dev\\build.xml";
    static String taskNum;
    static String projectName;

    public static void main(String[] args) throws IOException, InterruptedException {
//        taskNum = args[0];
//        projectName = args[1];
        Scanner sc = new Scanner(System.in);
        System.out.println("taskNum:");
        taskNum = sc.next();
        Spider spider = new Spider();
        Thread thread = new Thread(()->{
            try {
                spider.login();
            }catch (Exception e){
                System.out.println(e);
            }
        });
        thread.start();
        Thread.sleep(2000);
        System.out.println("-----输入peojectName-----");
        projectName = sc.next();
        Thread.sleep(3000);

        String filename = "D:\\git-dev\\" + projectName + "\\updatelist.txt";
        File f = new File(filename);
        if (!f.exists()) System.out.println("---文件不存在---");
        else {
            System.out.println("------输入\"y\"来确定是否推送uat------");
            String checkFile = sc.next();
            if ("y".equals(checkFile)) {
                antExecute();
                System.out.println("------输入\"y\"来确定是否继续构建流水线------");
                String isContinue = sc.next();
                if ("y".equals(isContinue)) {
                    PipelineAutoBuild pipelineAutoBuild = new PipelineAutoBuild();
                    pipelineAutoBuild.getToken();
                    pipelineAutoBuild.getCookie();
                    pipelineAutoBuild.autoBuild(projectName);
                }
            }
        }
        sc.close();
        Runtime.getRuntime().exit(0);
    }

    public void login() throws IOException, InterruptedException {
        //var uri = "http://192.168.11.166/zentao/user-login.html";
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

        var taskuri = "http://192.168.11.166/zentao/task-view-" + taskNum;
        HtmlPage taskPage = webclient.getPage(taskuri);
        var docu = Jsoup.parse(taskPage.asXml());
//        Element link = docu.selectFirst("ul.files-list")
//                .select("a[target]")
//                .select("a:not([onclick*=废弃])").get(0);

        System.out.println(docu.selectFirst("span.text").text());

        Elements links = docu.selectFirst("ul.files-list").select("a[target]");
        System.out.println("-----输出附件-----");
        links.stream().forEach(i-> System.out.println(i.text()));

        Element link = links.select("a:not([onclick*=废弃])").get(0);
        System.out.println("-----选择的附件-----");
        System.out.println(link.text());

        var fileLink = "http://192.168.11.166" + link.attr("href");

        System.out.println("==============");
        System.out.println("ProjectName:");
        while (projectName == null){
            Thread.sleep(1000);
        }

        URL url = new URL(fileLink);
        String filename = "D:\\git-dev\\" + projectName + "\\updatelist.txt";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
             FileWriter writer = new FileWriter(filename);) {

            Deque<String> deque = new ArrayDeque<>();
            String s = reader.readLine();
            if (s != null)
                deque.offer(s);
            while (!deque.isEmpty() || s.length() >= 0) {
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

    public static void antExecute() {
        Project project = new Project();
        try {
            DefaultLogger dl = new DefaultLogger();
            dl.setOutputPrintStream(System.out);
            dl.setOutputPrintStream(System.err);
            dl.setMessageOutputLevel(Project.MSG_INFO);
            project.addBuildListener(dl);
            project.fireBuildStarted();
            project.setProperty("pn", projectName);
            project.setProperty("cr", taskNum);
            project.init();

            ProjectHelper ph = ProjectHelper.getProjectHelper();
            File xmlFile = new File(xml);
            ph.parse(project, xmlFile);
            project.executeTarget("uat");
            project.fireBuildFinished(null);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}