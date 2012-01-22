package net.probot.rsc.loaders;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class WebCrawler {
    public WebCrawler() {
    }

    public static Map<String, String> getParameters(int worldID) {
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            Pattern regex = Pattern.compile("<param name=([^\\s]+)\\s+value=([^>]*)>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(sendGetRequest("http://classic"+worldID+".runescape.com/plugin.js?param=o0,a0,s0"));
            while (regexMatcher.find())
                if (!map.containsKey(regexMatcher.group(1)))
                    map.put(regexMatcher.group(1), regexMatcher.group(2));
            Thread.sleep(20);
            return map;
        } catch (PatternSyntaxException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        return null;
    }

    public static BufferedReader openCon(String url) throws Exception{
		return new BufferedReader(
			new InputStreamReader(
				new URL(url).openStream()));
    }

    public static String sendGetRequest(String urlStr) {
        String result = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
