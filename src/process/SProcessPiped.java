package process;

import java.io.IOException;

import process.exceptions.SProcessNotYetStartedException;

public abstract class SProcessPiped extends SProcess{

	SProcessPiped inputPipe = null;
	SProcessPiped outputPipe = null;
	SProcessPiped errorPipe = null;
	

	@Override
	public String getNormalOutput() throws SProcessNotYetStartedException{
		if(outputPipe == null){
			return super.getNormalOutput();
		}
		return null;
	}
	
	
	@Override
	public String getErrorOutput() throws SProcessNotYetStartedException{
		if(errorPipe == null){
			return super.getErrorOutput();
		}
		return null;
	}
	
	@Override
	public void writeToProcessStdIn(String input) throws SProcessNotYetStartedException, IOException{
		if(inputPipe == null){
			super.writeToProcessStdIn(input);
		}
		throw new IOException("Process sdtIn is already in use by other process. (Did you connect a pipe?)");
	}
}
