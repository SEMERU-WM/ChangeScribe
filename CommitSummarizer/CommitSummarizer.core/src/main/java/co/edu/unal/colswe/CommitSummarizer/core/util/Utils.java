package co.edu.unal.colswe.CommitSummarizer.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}
	
	public static File inputStreamToFile(InputStream is) throws IOException {
		File contentFile = File.createTempFile("tmpCont", ".txt");

		OutputStream outputStream = null;
		outputStream = new FileOutputStream(contentFile);
		
		int read = 0;
		byte[] bytes = new byte[1024];
 
		while ((read = is.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		
		outputStream.close();
		
		return contentFile;
		
	}

}
