package shellcommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
	private String result = "";
	
	// Error output from process
	private String error = "";
	
	//Buffered readers for the Process stdout and stderror.
	private BufferedReader stdNormalOut;
	private BufferedReader stdError;
	
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
	 * Get the normal output from the process (process stdout)
	 * 
	 * @return
	 * The process stdout.
	 * Null if command has not been executed yet.
	 * 
	 */
	public String getNormalOutput(){
		if(this.process == null || this.stdNormalOut == null){
			return null;
		}
		
		String line = "";;
		try {
			while ((line = this.stdNormalOut.readLine()) != null) {
			    this.result = this.result + line + "\n";
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		return this.result;
	}
	
	
	/**
	 * Get the normal output from the process (process stderror)
	 * 
	 * @return
	 * The process stderror.
	 * Null if command has not been executed yet.
	 * 
	 */
	public String getErrorOutput(){
		if(this.process == null || this.stdError == null){
			return null;
		}
		
		String line = "";;
		try {
			while ((line = this.stdError.readLine()) != null) {
			    this.error = this.error + line + "\n";
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		return this.error;
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
