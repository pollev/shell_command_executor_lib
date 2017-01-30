package example;

import java.io.IOException;

import process.SProcess;
import process.SProcessExecutor;
import process.NonMatchingOSException;
import process.SProcessNotYetStartedException;
import process.SProcessExecutor.OS;

public class TestInputCommand extends SProcess{

	@Override
	public String getCommand() {
		return "cat";
	}

	@Override
	public OS getOSType() {
		return OS.UNIX;
	}
	
	public static void main(String[] args) {

		SProcessExecutor ex = SProcessExecutor.getCommandExecutor();
		SProcess command = new TestInputCommand();
		try {
			ex.executeCommand(command);
			System.out.println("command: " + command.getCommand());
			System.out.println("status: " + command.getStatus() + "\n\n");
			
			command.writeToProcessStdIn("hello world");
			
			//command.waitForCompletion();
			
			System.out.println("status: " + command.getStatus() + "\n\n");

			if(command.waitForOutput()){
				System.out.println("output: " + command.getNormalOutput());
			}else{
				System.out.println("error: " + command.getErrorOutput());				
			}
			
			System.out.println("part 2");
			command.writeToProcessStdIn("second input");
			if(command.waitForOutput()){
				System.out.println("output: " + command.getNormalOutput());
			}else{
				System.out.println("error: " + command.getErrorOutput());				
			}
			
			System.out.println("\n \n end output");

		} catch (NonMatchingOSException | IOException | SProcessNotYetStartedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
