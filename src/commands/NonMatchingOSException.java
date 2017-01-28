package commands;

public class NonMatchingOSException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NonMatchingOSException(CommandExecutor.OS executorType, CommandExecutor.OS commandType){
		super("Mismatch between executor type (" + executorType + ") and command type (" + commandType + ").");
	}

}
