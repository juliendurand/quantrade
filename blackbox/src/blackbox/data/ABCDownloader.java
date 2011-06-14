package blackbox.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class ABCDownloader {

	private static final String fileName = Downloader.PATH + "tickers.csv";

	private static String getTickers() throws UnsupportedEncodingException,
			IOException, ClientProtocolException {
		HttpPost post = new HttpPost(
				"http://www.abcbourse.com/download/libelles.aspx");
		post.addHeader(
				"Accept",
				"application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters
				.add(new BasicNameValuePair(
						"__VIEWSTATE",
						"/wEPDwUINjE4MzY0MTBkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYjBRJjdGwwMCRJbWFnZUJ1dHRvbjEFEmN0bDAwJEJvZHlBQkMkc3JkcAUUY3RsMDAkQm9keUFCQyRzcmRsb3AFGGN0bDAwJEJvZHlBQkMkaW5kaWNlc0ZScAUYY3RsMDAkQm9keUFCQyRldXJvbGlzdEFwBRhjdGwwMCRCb2R5QUJDJGV1cm9saXN0QnAFGGN0bDAwJEJvZHlBQkMkZXVyb2xpc3RDcAUZY3RsMDAkQm9keUFCQyRldXJvbGlzdHplcAUaY3RsMDAkQm9keUFCQyRldXJvbGlzdGh6ZXAFFGN0bDAwJEJvZHlBQkMkYWx0ZXJwBRRjdGwwMCRCb2R5QUJDJG5tc3BlcAURY3RsMDAkQm9keUFCQyRtbHAFEmN0bDAwJEJvZHlBQkMkbWxlcAUUY3RsMDAkQm9keUFCQyR0cmFja3AFE2N0bDAwJEJvZHlBQkMkcG10cHAFEWN0bDAwJEJvZHlBQkMkYnNwBRNjdGwwMCRCb2R5QUJDJG9ibDJwBRZjdGwwMCRCb2R5QUJDJHdhcnJhbnRzBRJjdGwwMCRCb2R5QUJDJGZjcHAFFWN0bDAwJEJvZHlBQkMkeGNhYzQwcAUWY3RsMDAkQm9keUFCQyR4c2JmMTIwcAUVY3RsMDAkQm9keUFCQyR4Y2FjYXRwBRZjdGwwMCRCb2R5QUJDJHhjYWNuMjBwBRhjdGwwMCRCb2R5QUJDJHhjYWNzbWFsbHAFFWN0bDAwJEJvZHlBQkMkeGNhYzYwcAUVY3RsMDAkQm9keUFCQyR4Y2FjbXNwBRVjdGwwMCRCb2R5QUJDJHhiZWwyMGcFFWN0bDAwJEJvZHlBQkMkeGFleDI1bgURY3RsMDAkQm9keUFCQyRkanUFEmN0bDAwJEJvZHlBQkMkbmFzdQUSY3RsMDAkQm9keUFCQyRiZWxnBRNjdGwwMCRCb2R5QUJDJGhvbGxuBRVjdGwwMCRCb2R5QUJDJGxpc2JvYWwFEmN0bDAwJEJvZHlBQkMkZGV2cAUaY3RsMDAkQm9keUFCQyRJbWFnZUJ1dHRvbjHpieeqHeIFCH1DlRzvblGsEfIr8w=="));
		// parameters.add(new BasicNameValuePair("__EVENTTARGET", ""));
		// parameters.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
		parameters
				.add(new BasicNameValuePair(
						"__EVENTVALIDATION",
						"/wEWJQL58tAdAo+CnjsC7LLy0AMCzsPYqw4CzM7D7gECt7vrug0C6ey36QwCmLHeqQ4Cs5r8vggC58Gmvw4C2qfbwg4Cl4yI/QYC2PCj0wsC3LqTugIC8brTugICpqOX+QgCwsOgvgECz+3rxA8Ci4u3xg0C9Nmq0gsC48j0ogICl5jS5Q0CsYn5lwsCoMSE1A0C9e377AwCmZrc5gsCzeqNkAIC5LLOmgMC+sauvAwC88aGwwQCnpu8mAgCjv/pwg8C6+3nrgwC/PC9+gcCnOOzhwECn5vYgwgC9aDN9gh5NAMpv9uGgiyOV61kL4NR+/x42A=="));
		// parameters.add(new BasicNameValuePair("ctl00$txtAutoComplete",""));
		parameters.add(new BasicNameValuePair("ctl00$BodyABC$srdp", "on"));
		parameters.add(new BasicNameValuePair("ctl00$BodyABC$eurolistAp", "on"));
		//parameters.add(new BasicNameValuePair("ctl00$BodyABC$eurolistBp", "on"));
		//parameters.add(new BasicNameValuePair("ctl00$BodyABC$eurolistCp", "on"));
		//parameters.add(new BasicNameValuePair("ctl00$BodyABC$alterp", "on"));
		//parameters.add(new BasicNameValuePair("ctl00$BodyABC$mlterp", "on"));
		parameters.add(new BasicNameValuePair("ctl00$BodyABC$ImageButton1.x","16"));
		parameters.add(new BasicNameValuePair("ctl00$BodyABC$ImageButton1.y","24"));

		UrlEncodedFormEntity sendentity = new UrlEncodedFormEntity(parameters,
				HTTP.UTF_8);
		post.setEntity(sendentity);

		// create the client and execute the post method
		HttpClient client = new DefaultHttpClient();
		HttpResponse postResponse = client.execute(post);

		// Output the Response from the POST
		String content = Downloader.convertStreamToString(postResponse.getEntity()
				.getContent());

		// releasing POST
		EntityUtils.consume(postResponse.getEntity());
		client.getConnectionManager().shutdown();
		return content;
	}

	public static void downloadTickers() throws UnsupportedEncodingException,
			IOException, ClientProtocolException, Exception {
		System.out.print("Downloading list of tickers");
		String content = getTickers();
		Downloader.saveToFile(content, fileName);
		System.out.println(" -> OK");
	}
	
	public static List<String> getAllTickers(){
		List<String> tickers = new ArrayList<String>();
		try{
			FileReader reader = new FileReader(fileName);
			BufferedReader in = new BufferedReader(reader);
			//discard header line (first line of the file)
			in.readLine();
			String line = "";
			while ((line = in.readLine()) != null)   {
				int firstSemicolumnPosition = line.indexOf(";", 0)+1;
				int secondSemicolumnPosition = line.indexOf(";", firstSemicolumnPosition)+1;
				int thirdSemicolumnPosition = line.indexOf(";", secondSemicolumnPosition)+1;
				String ticker = line.substring(thirdSemicolumnPosition)+".PA";
				tickers.add(ticker);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(tickers);
		return tickers;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		downloadTickers();
		getAllTickers();
	}

}
