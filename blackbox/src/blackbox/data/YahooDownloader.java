package blackbox.data;

import java.util.List;

public class YahooDownloader {
	
	private static String getTimeSeries(String ticker) {

		String url = "http://ichart.finance.yahoo.com/table.csv?s="+ticker+"&ignore=.csv";
		return Downloader.downloadFile(url);
	}
	
	private static String getDividends(String ticker){
		String url = "http://ichart.finance.yahoo.com/table.csv?s="+ticker+"&g=v&ignore=.csv";
		return Downloader.downloadFile(url);
	}
	
	public static void downloadAllFiles(){
		List<String> tickers = ABCDownloader.getAllTickers();
		int i = 1;
		int size = tickers.size();
		for(String ticker : tickers){
			System.out.print("Downloading data for "+ticker+" ["+ i++ +"/"+size+"]");
			try {
				Downloader.saveToFile(getTimeSeries(ticker), Downloader.PATH + ticker+".ts");
				Downloader.saveToFile(getDividends(ticker), Downloader.PATH + ticker+".dvd");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(" -> OK");
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		downloadAllFiles();
		System.out.println("DONE");
		}

}
