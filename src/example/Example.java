package example;

import java.io.IOException;

import shellcommand.Command;
import shellcommand.CommandExecutor;
import shellcommand.NonMatchingOSException;
import shellcommand.ProcessNotYetStartedException;

public class Example {

	public static void main(String[] args) {

		CommandExecutor ex = CommandExecutor.getCommandExecutor();
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
