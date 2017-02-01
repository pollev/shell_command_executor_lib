package process;

import java.io.IOException;

import process.exceptions.SProcessNotYetStartedException;

public abstract class SProcessPiped extends SProcess{

	private SProcessPiped inputPipe = null;
	private SProcessPiped outputPipe = null;
	private SProcessPiped errorPipe = null;
	
	
	/**
	 * Set the given process as the output pipe for this process
	 * Set this process as the input pipe for the given process
	 * 
	 * @param process to pipe output data to
	 */
	public void PipeOutputTo(SProcessPiped process){
		this.outputPipe = process;
		process.inputPipe = this;
		
		PipeThread pipe = new PipeThread(this.stdNormalOut, process.stdInput, this);
		(new Thread(pipe)).start();
	}
	
	
	/**
	 * Set the given process as the error pipe for this process
	 * Set this process as the input pipe for the given process
	 * 
	 * @param process to pipe error data to
	 */
	public void PipeErrorTo(SProcessPiped process){
		this.errorPipe = process;
		process.inputPipe = this;
		
		PipeThread pipe = new PipeThread(this.stdError, process.stdInput, this);
		(new Thread(pipe)).start();
	}
	
	
	/*
	 * 
	 * ********************************************************************
	 * Override normal IO methods if a pipe is connected to its datastream.
	 * ********************************************************************
	 * 
	 */

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
	
	
	@Override
	public boolean waitForOutput() throws IOException{
		if(outputPipe == null && errorPipe == null){
			return super.waitForOutput();
		}
		throw new IOException("Process stdOut or stdError is already in use by other process. (Did you connect a pipe?)");
	}
	
	
	@Override
	public void waitForOutputNormal() throws IOException{
		if(outputPipe == null){
			super.waitForOutputNormal();
		}
		throw new IOException("Process stdOut is already in use by other process. (Did you connect a pipe?)");
	}
	
	
	@Override
	public void waitForOutputError() throws IOException{
		if(errorPipe == null){
			super.waitForOutputError();
		}
		throw new IOException("Process stdError is already in use by other process. (Did you connect a pipe?)");
	}
	
	
	
}
