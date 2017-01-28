package shellcommand;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles command objects and executes them. 
 * It also provides support for checking the OS that we run on.
 * 
 * @author polle
 *
 */
public class CommandExecutor {

	/**
	 * Map of OS types to command executors, ensures only one executor per OS type.
	 */
	private static HashMap<OS, CommandExecutor> singleton = new HashMap<>();
	
	// logger
	final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);	
	
	/**
	 * OS type of this executor (for example a windows executor), this ensures no commands designed for a different OS get executed.
	 */
	private final OS OSType;
	
	
	/**
	 * This is a private constructor. Access to this class should happen through getCommandExecutor().
	 * 
	 * @param OSType
	 */
	private CommandExecutor(OS OSType){
		this.OSType = OSType;
	}
	
	
	/**
	 * Execute the given command and link the command object to the newly created process.
	 * This will also verify that the OS type of the command matches this executor.
	 * 
	 * NOTE:
	 * 		Commands with the OS type "ANY" will be executed by all executors.
	 * 		Executors with the OS type "ANY" will only execute commands with command type "ANY"
	 * 			(this makes sense because the OS could not be determined for that executor)
	 * 
	 * @param command
	 * 		The command object that must be executed on the system
	 * @throws NonMatchingOSException 
	 * 		The OS type the command is designed for does not match the command executor type
	 * @throws IOException 
	 * 		An IOException occurred while executing the command.
	 */
	public void executeCommand(Command command) throws NonMatchingOSException, IOException{
		if (command.getOSType() == this.OSType || command.getOSType() == OS.ANY){
			try {
				Process p = null;;
				if(this.OSType == OS.UNIX){ // If unix based pass command through /bin/sh to enable support for piping
					String[] commandarr = {"/bin/sh", "-c", command.getCommand()};
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
	public static CommandExecutor getCommandExecutor(){
		OS OSType = detectOSType();
		if(CommandExecutor.singleton.get(OSType) == null){
			CommandExecutor.singleton.put(OSType, new CommandExecutor(OSType));
		}
		return CommandExecutor.singleton.get(OSType);
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
	 * Get the OS type of this CommandExecutor
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
	 * 		For Commands: In case commands should work on any OS
	 * 
	 */
	public enum OS{
		WINDOWS, UNIX, MAC, ANY;
	}
	
	
}
