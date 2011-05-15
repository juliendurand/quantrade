package blackbox.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVFileReader {

	public static List<String[]> readFile(String filename) throws Exception{
		File file = new File(filename);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		List<String[]> rows = new ArrayList<String[]>();
		String line ="";
		while((line=br.readLine())!=null){
			List<String> values = new ArrayList<String>();
			int semicolumnPosition;
			while((semicolumnPosition=line.indexOf(','))>0){
				values.add(line.substring(0, semicolumnPosition));
				line = line.substring(semicolumnPosition+1);	
			}
			values.add(line);
			rows.add(values.toArray(new String[values.size()]));
			}
		return rows;
	}
	
	public static void main(String[] args) throws Exception {
		List<String[]> content = readFile("/Users/juliendurand/data/raw/ALUAN.PA.ts");
		for(String[] row : content){
			for(String value : row){
				System.out.print(value+" ");
			}
			System.out.println();
		}
		
	}
	
}
