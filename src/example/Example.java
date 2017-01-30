package example;

import java.io.IOException;

import process.SProcess;
import process.SProcessExecutor;
import process.exceptions.NonMatchingOSException;
import process.exceptions.SProcessNotYetStartedException;

public class Example {

	public static void main(String[] args) {

		SProcessExecutor ex = SProcessExecutor.getCommandExecutor();
		SProcess command = new ExampleCommand();
		try {
			ex.executeCommand(command);
			command.waitForCompletion();
			System.out.println(command.getNormalOutput());
		} catch (NonMatchingOSException | IOException | SProcessNotYetStartedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
