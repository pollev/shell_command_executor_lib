package example;

import process.SProcess;
import process.SProcessExecutor.OS;

public class ExampleCommand extends SProcess{

	@Override
	public String getCommand() {
		return "ls";
	}

	@Override
	public OS getOSType() {
		return OS.UNIX;
	}

}
