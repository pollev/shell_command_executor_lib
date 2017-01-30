package shellcommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class for representing a shell command and its status.
 * A command is defined by the OS it's designed for and the actual command string.
 * Commands can be executed by the CommandExecutor class.
 * 
 * @author polle
 *
 */
public abstract class Command {
	
	// logger
	final Logger logger = LoggerFactory.getLogger(Command.class);	
	
	// Process tied to this command, null if not yet executed by CommandExecutor.
	private Process process = null;
	
	// Normal output from process
	private String resultHistory = "";
	
	// Error output from process
	private String errorHistory = "";
	
	//Buffered readers for the Process stdout and stderror.
	private BufferedReader stdNormalOut;
	private BufferedReader stdError;
	private BufferedWriter stdInput;
	
	/**
	 * This method must be implemented to return the desired command string
	 * Examples:
	 * 		- return "echo hello world"
	 * 		- return "ls"
	 * 
	 * @return
	 * The command that should be executed.
	 * 
	 */
	public abstract String getCommand();
	
	/**
	 * This method must be implemented to return the desired OS type.
	 * Examples:
	 * 		- return CommandExecutor.OS.WINDOWS;
	 * 		- return CommandExecutor.OS.LINUX
	 * 
	 * @return
	 * The OS Type for this command
	 * 
	 */
	public abstract CommandExecutor.OS getOSType();
	
	/**
	 * This method is called by the CommandExecutor to set the process connected to this command
	 * 
	 * @param process the process connected to this command
	 * 
	 */
	protected void setProcessHandle(Process process){
		this.process = process;
		this.stdNormalOut = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
		this.stdError = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
		this.stdInput = new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream()));
	}
	
	
	/**
	 * Get the status for this Command.
	 * 
	 * @return
	 * NOT_YET_EXCECUTED: This command has not yet been executed by the CommandExecutor
	 * WAITING_FOR_COMPLETION: This command is currently processing
	 * COMPLETED_NORMAL: normal completion
	 * COMPLETED_ERROR: command exited with a non zero error code. Use getExitCode() to view
	 * 
	 */
	public STATUS getStatus(){
		if(this.process == null){
			return STATUS.NOT_YET_EXCECUTED;
		}else if(this.process.isAlive()){
			return STATUS.WAITING_FOR_COMPLETION;
		}else{
			try{
				if(process.exitValue() == 0){
					return STATUS.COMPLETED_NORMAL;
				}else{
					return STATUS.COMPLETED_ERROR;
				}
			}catch(IllegalThreadStateException e){
				return STATUS.WAITING_FOR_COMPLETION;
			}
		}
	}
	
	
	/**
	 * Get the exit code for this process
	 * 
	 * @return
	 * The exit code for this process. null if none is available.
	 * 
	 */
	public Integer getExitCode(){
		if(this.process == null)
			return null;
		try{
			return process.exitValue();			
		}catch(IllegalThreadStateException e){
			return null;
		}
	}
	
	
	/**
	 * Get all the currently available, not read, normal output from the process (process stdout)
	 * 
	 * @return
	 * 		The process stdout.
	 * 		An empty String if no output is available
	 * @throws ProcessNotYetStartedException 
	 * 		If the Process has not yet been executed by a CommandExecutor
	 * 
	 */
	public String getNormalOutput() throws ProcessNotYetStartedException{
		if(this.process == null || this.stdNormalOut == null){
			throw new ProcessNotYetStartedException(this);
		}
		
		String output = "";
		char ch;
		try {
			while (this.stdNormalOut.ready()) {
				ch = (char) this.stdNormalOut.read();
			    output= output + ch;
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		this.resultHistory = this.resultHistory + output;
		return output;
	}
	
	
	/**
	 * Get all data already read from the process stdOut.
	 * NOTE: Does not read any new data, data is read using getNormalOutput()
	 * 
	 * @return
	 * 		All data already read from the process stdOut
	 * 
	 */
	public String getNormalOutputHistory(){
		return this.resultHistory;
	}
	
	
	/**
	 * Get all the currently available, not read, error output from the process (process stdout)
	 * 
	 * @return
	 * 		The process stderror.
	 * 		An empty String if no output is available
	 * @throws ProcessNotYetStartedException 
	 * 		If the Process has not yet been executed by a CommandExecutor
	 * 
	 */
	public String getErrorOutput() throws ProcessNotYetStartedException{
		if(this.process == null || this.stdError == null){
			throw new ProcessNotYetStartedException(this);
		}
		
		String output = "";
		char ch;
		try {
			while (this.stdError.ready()) {
				ch = (char) this.stdError.read();
			    output= output + ch;
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		this.errorHistory = this.errorHistory + output;
		return output;
	}
	
	
	/**
	 * Get all data already read from the process stdError.
	 * NOTE: Does not read any new data, data is read using getErrorOutput()
	 * 
	 * @return
	 * 		All data already read from the process stdOut
	 * 
	 */
	public String getErrorOutputHistory(){
		return this.errorHistory;
	}
	
	
	/**
	 * Write the input String into the standard input for the process spawned by the command.
	 * 
	 * @param input
	 * 		The String to feed into the stdInput of the process spawned by the command
	 * @throws ProcessNotYetStartedException
	 * 		The command has not yet been executed by a CommandExecutor
	 * @throws IOException
	 * 		An IOException occurred while writing to the stdInput
	 */
	//TODO: Overload method for byte input
	public void writeToProcessStdIn(String input) throws ProcessNotYetStartedException, IOException{
		if(this.process == null){
			throw new ProcessNotYetStartedException(this);
		}
		this.stdInput.write(input);
		this.stdInput.flush();
	}
	
	
	/**
	 * This method actively waits until the child process has data available:
	 * 	- On its stdOut: returns true
	 * 	- On its stdError: returns false
	 * or 
	 * @return
	 * @throws IOException 
	 */
	public boolean waitForOutput() throws IOException{
		while(!this.stdNormalOut.ready() && !this.stdError.ready()){
			// active waiting untill one is ready
		}
		if(this.stdNormalOut.ready()){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * Throws a ProcessNotYetStartedException exception if the process has not been executed by the 
	 * commandExecutor. Otherwise executes Process.waitfor(). See the doc on Process for more information.
	 * 
	 * @throws ProcessNotYetStartedException 
	 * 		The process has not yet been started
	 * @throws InterruptedException
	 * 		The current thread was interrupted while waiting for the process to finish
	 */
	public void waitForCompletion() throws ProcessNotYetStartedException, InterruptedException{
		if(this.process == null){
			throw new ProcessNotYetStartedException(this);
		}
		this.process.waitFor();
	}
	
	
	/**
	 * This enum represents the possible states a command can be in.
	 */
	public enum STATUS{
		NOT_YET_EXCECUTED, WAITING_FOR_COMPLETION, COMPLETED_NORMAL, COMPLETED_ERROR;
	}

}
