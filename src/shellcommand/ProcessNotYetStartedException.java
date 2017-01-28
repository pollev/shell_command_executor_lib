package shellcommand;

public class ProcessNotYetStartedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ProcessNotYetStartedException(Command command){
		super("Could not wait on command process to terminate because it has not yet been started."
				+ "Command string is: \"" + command.getCommand() + "\"");
	}

}
