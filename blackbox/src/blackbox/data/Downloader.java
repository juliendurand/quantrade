package blackbox.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Downloader {
	
	public final static String PATH = "/Users/juliendurand/data/raw/";
	
	public static String downloadFile(String url){
		InputStream contentStream = null;
		String content = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = httpclient.execute(new HttpGet(url));
		    contentStream = response.getEntity().getContent();
		    content = convertStreamToString(contentStream);
		    //System.out.println(content);
		} catch (Exception e) {
			System.err.println("Yahoo Downloader - Error processing Get request.");
		}	
		return content;
	}
	
	public static void saveToFile(String content, String filename) throws Exception{
		File file = new File(filename);
		if(!file.exists()){
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(file);
		writer.write(content);
		writer.flush();
		writer.close();
	}
	
	public static String convertStreamToString(InputStream is)
	throws IOException {

		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */

		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

}
