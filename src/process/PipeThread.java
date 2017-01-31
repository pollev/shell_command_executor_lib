package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeThread implements Runnable{

	private final Logger logger = LoggerFactory.getLogger(PipeThread.class);
	
	private final BufferedReader in;
	private final BufferedWriter out;
	
	public PipeThread(BufferedReader in, BufferedWriter out){
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		try{
			while(true){
				// We really want to start off with a blocking read to avoid active waiting
				char ch = (char) in.read();
				if(Character.getNumericValue(ch) == -1){ // We need to check if the character indicates end of stream
					break;
				}
				String data =""+ ch;
				while(in.ready()){ // Now read all available characters
					ch = (char) in.read();
					System.out.println("char: " + Character.getNumericValue(ch));
					if(Character.getNumericValue(ch) == -1){ // We don't want to write end of stream to other process
						break;
					}
					data = data + ch;
				}
				out.write(data);
				out.flush();
				if(Character.getNumericValue(ch) == -1){ // Check if the last character read indicated end of stream
					break;
				}
			}
			in.close();
			out.close();
		}catch(IOException e){
			logger.error(e.getMessage());
		}
	}

}
