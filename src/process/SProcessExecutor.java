package process;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import process.exceptions.NonMatchingOSException;

/**
 * This class handles SProcess objects and executes them. 
 * It also provides support for checking the OS that we run on.
 * 
 * @author polle
 *
 */
public class SProcessExecutor {

	/**
	 * Map of OS types to SProcess executors, ensures only one executor per OS type.
	 */
	private static HashMap<OS, SProcessExecutor> singleton = new HashMap<>();
	
	// logger
	final Logger logger = LoggerFactory.getLogger(SProcessExecutor.class);	
	
	/**
	 * OS type of this executor (for example a windows executor), this ensures no commands designed for a different OS get executed.
	 */
	private final OS OSType;
	
	
	/**
	 * This is a private constructor. Access to this class should happen through getSProcessExecutor().
	 * 
	 * @param OSType
	 */
	private SProcessExecutor(OS OSType){
		this.OSType = OSType;
	}
	
	
	/**
	 * Execute the given SProcess and link the SProcess object to the newly created process.
	 * This will also verify that the OS type of the SProcess matches this executor.
	 * 
	 * NOTE:
	 * 		SProcesses with the OS type "ANY" will be executed by all executors.
	 * 		Executors with the OS type "ANY" will only execute SProcesses with OS type "ANY"
	 * 			(this makes sense because the OS could not be determined for that executor)
	 * 
	 * @param command
	 * 		The SProcess object that must be executed on the system
	 * @throws NonMatchingOSException 
	 * 		The OS type the command SProcess is designed for does not match the executor type
	 * @throws IOException 
	 * 		An IOException occurred while executing the command.
	 */
	public void executeCommand(SProcess command) throws NonMatchingOSException, IOException{
		if (command.getOSType() == this.OSType || command.getOSType() == OS.ANY){
			try {
				Process p = null;
				// Check OS to enable piping support in linux and windows
				if(this.OSType == OS.UNIX){
					String[] commandarr = {"/bin/sh", "-c", command.getCommand()};
					p = Runtime.getRuntime().exec(commandarr);
				}else if(this.OSType == OS.WINDOWS){
					String[] commandarr = {"cmd /C", command.getCommand()};
					p = Runtime.getRuntime().exec(commandarr);
				}else{
					p = Runtime.getRuntime().exec(command.getCommand());
				}
				command.setProcessHandle(p);
			} catch (IOException e) {
				logger.error("IO exception while executing command \"" + command.getCommand() + "\"");
				throw e;
			}
		}else{
			NonMatchingOSException OSExc = new NonMatchingOSException(this.OSType, command.getOSType());
			logger.warn(OSExc.getMessage());
			throw OSExc;
		}
	}
	
	
	/**
	 * This method will detect the current OS type and returns a appropriate executor for that OS type.
	 * At any point, there is only one executor for each OS type.
	 * 
	 * @return 
	 * 		The singleton CommandExecutor object for the detected OS type.
	 */
	public static SProcessExecutor getCommandExecutor(){
		OS OSType = detectOSType();
		if(SProcessExecutor.singleton.get(OSType) == null){
			SProcessExecutor.singleton.put(OSType, new SProcessExecutor(OSType));
		}
		return SProcessExecutor.singleton.get(OSType);
	}
	
	
	/**
	 * Detect the OS type that is currently running.
	 * 
	 * @return 
	 * 		The current OS type
	 * 		Returns OS.ANY if the OS type could not be determined
	 */
	private static OS detectOSType(){
		String os = System.getProperty("os.name").toLowerCase();
		OS detected;
		if(os.contains("win")){
			detected = OS.WINDOWS;
		}else if(os.contains("nix") || os.contains("nux") || os.contains("aix") ){
			detected = OS.UNIX;
		}else if(os.contains("mac")){
			detected = OS.MAC;
		}else{
			detected = OS.ANY;
		}
		
		return detected;
	}
	
	
	/**
	 * Get the OS type of this SProcessExecutor
	 * 
	 * @return 
	 * 		the OS type of this executor
	 * 
	 */
	public OS getOSType(){
		return this.OSType;
	}
	
	/**
	 * Enum representing the possible OS types
	 * Currently supports:
	 * 		- WINDOWS
	 * 		- LINUX
	 * 		- MAC
	 * 
	 * ANY:
	 * 		For Executor: In case OS type could not be determined or is not supported
	 * 		For SProcesses: In case commands should work on any OS
	 * 
	 */
	public enum OS{
		WINDOWS, UNIX, MAC, ANY;
	}
	
	
}
