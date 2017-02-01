package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeThread implements Runnable{

	private final Logger logger = LoggerFactory.getLogger(PipeThread.class);
	
	private final SProcessPiped sender;
	private final BufferedReader in;
	private final BufferedWriter out;
	
	public PipeThread(BufferedReader in, BufferedWriter out, SProcessPiped sender){
		this.in = in;
		this.out = out;
		this.sender = sender;
	}
	
	@Override
	public void run() {
		try{
			while(true){
				// We really want to start off with a blocking read to avoid active waiting
				char ch = (char) in.read();
				String data =""+ ch;
				while(in.ready()){ // Now read all available characters
					ch = (char) in.read();
					System.out.println("char: " + Character.getNumericValue(ch));
					data = data + ch;
				}
				out.write(data);
				out.flush();
				if(this.sender.getStatus() == SProcess.STATUS.COMPLETED_NORMAL){
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
