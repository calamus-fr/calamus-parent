package fr.calamus.common.tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class LogAndConsoleOutputStream extends OutputStream {
	
	private OutputStream consoleOut;
	//private File fileOut;
	private RandomAccessFile raf;
	private String prefix;
	
	public LogAndConsoleOutputStream(OutputStream consoleOut, RandomAccessFile raf, boolean normalOutput){
		this.consoleOut=consoleOut;
		this.raf=raf;
		this.prefix=normalOutput?"OUT: ":"ERR: ";
	}

	@Override  
	public void write(int b) throws IOException {  
		//addLine(String.valueOf((char) b));  
		addLine(String.valueOf((char) b).replaceAll("\r", ""));
		consoleOut.write(b);
	}  
	  
	@Override  
	public void write(byte[] b, int off, int len) throws IOException {  
		//addLine(new String(b, off, len));  
		addLine(new String(b, off, len));
		consoleOut.write(b, off, len);
	}  
	  
	@Override  
	public void write(byte[] b) throws IOException {  
		write(b, 0, b.length);  
	}
	protected void addLine(String s) {
		/*if(raf==null){
			initRaf();
		}*/
		if(raf!=null){
			try {
				raf.writeBytes(prefix+s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
