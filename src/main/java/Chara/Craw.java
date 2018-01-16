package Chara;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Craw implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    private final static String prefix = "http://www.chineseetymology.org/";
    private static Spider spider ;
    public void process(Page page) {
        String simple = page.getHtml().xpath("//*[@id=\"SimplifiedChar\"]/text()").toString();
        String tradition = page.getHtml().xpath("//*[@id=\"TraditionalChar\"]/text()").toString();
        File dir = null;
        if (!"-".equals(simple)){
            dir = new File("d://graduate/"+simple);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            List<String> srcs = page.getHtml().xpath("//*[@id=\"LstImages\"]/table/tbody/tr/td/img/@src").all();
            for (String src : srcs){
                try {
                    String[] slice = src.split("/");
                    String suffix = slice[slice.length-1];
                    download(prefix+src,dir.getAbsolutePath()+"/"+suffix);
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                    spider.addUrl(page.getUrl().toString());
                }
            }
        } else {
            if (!"-".equals(tradition)){
                dir = new File("d://graduate/"+tradition);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                List<String> srcs = page.getHtml().xpath("//*[@id=\"LstImages\"]/table/tbody/tr/td/img/@src").all();
                for (String src : srcs){
                    try {
                        String[] slice = src.split("/");
                        String suffix = slice[slice.length-1];
                        download(prefix+src,dir.getAbsolutePath()+"/"+suffix);
                        System.out.println("success");
                    } catch (Exception e) {
                        e.printStackTrace();
                        spider.addUrl(page.getUrl().toString());
                    }
                }
            } else {
                page.setSkip(true);
            }
        }

    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        spider = Spider.create(new Craw());
        String start = "4e00";
        String end  = "9fa5";
        for (int i = Integer.valueOf(start,16);i<=Integer.valueOf(end,16);i++) {
            spider.addUrl("http://www.chineseetymology.org/CharacterEtymology.aspx?characterInput=" + String.valueOf((char) i) + "&submitButton1=Etymology");
        }
        spider.thread(5).run();
    }

    public static void download(String urlString, String filename) throws Exception {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        byte[] bs = new byte[1024];
        int len;
        OutputStream os = new FileOutputStream(filename);
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        is.close();
    }
}
