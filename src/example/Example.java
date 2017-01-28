package example;

import java.io.IOException;

import shellcommand.Command;
import shellcommand.CommandExecutor;
import shellcommand.CommandExecutor.OS;
import shellcommand.NonMatchingOSException;
import shellcommand.ProcessNotYetStartedException;
import shellcommand.commands.ExampleCommand;

public class Example {

	public static void main(String[] args) {

		CommandExecutor ex = CommandExecutor.getCommandExecutor(OS.LINUX);
		Command command = new ExampleCommand();
		try {
			ex.executeCommand(command);
			command.waitForCompletion();
			System.out.println(command.getNormalOutput());
		} catch (NonMatchingOSException | IOException | ProcessNotYetStartedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
