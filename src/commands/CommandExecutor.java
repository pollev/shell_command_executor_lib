package commands;

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
	 * 
	 * @param command
	 * The command object that must be executed on the system
	 * @throws NonMatchingOSException 
	 * @throws IOException 
	 * 
	 */
	public void executeCommand(Command command) throws NonMatchingOSException, IOException{
		if (command.getOSType() == this.OSType){
			try {
				Process p = Runtime.getRuntime().exec(command.getCommand());
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
	
	
	public static CommandExecutor getCommandExecutor(OS OSType){
		if(CommandExecutor.singleton.get(OSType) == null){
			CommandExecutor.singleton.put(OSType, new CommandExecutor(OSType));
		}
		return CommandExecutor.singleton.get(OSType);
	}
	
	
	public enum OS{
		WINDOWS, LINUX;
	}
	
	
}
