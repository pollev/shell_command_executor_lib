package shellcommand.commands;

import shellcommand.Command;
import shellcommand.CommandExecutor.OS;

public class ExampleCommand extends Command{

	@Override
	public String getCommand() {
		return "ls";
	}

	@Override
	public OS getOSType() {
		return OS.UNIX;
	}

}
