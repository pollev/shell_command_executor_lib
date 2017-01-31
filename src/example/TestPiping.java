package example;

import process.SProcessExecutor.OS;
import process.exceptions.NonMatchingOSException;
import process.exceptions.SProcessNotYetStartedException;

import java.io.IOException;

import process.SProcessExecutor;
import process.SProcessPiped;

public class TestPiping {
	
	
	static class Cat extends SProcessPiped{

		@Override
		public String getCommand() {
			return "cat";
		}

		@Override
		public OS getOSType() {
			return OS.UNIX;
		}
		
	}
	
	
	static class Echo extends SProcessPiped{

		@Override
		public String getCommand() {
			return "echo Hello World";
		}

		@Override
		public OS getOSType() {
			return OS.UNIX;
		}
		
	}
	
	
	public static void main(String[] args) {

		SProcessExecutor ex = SProcessExecutor.getCommandExecutor();
		
		SProcessPiped echo = new Echo();
		SProcessPiped cat = new Cat();
		try {
			
			ex.executeCommand(echo);
			ex.executeCommand(cat);
			
			echo.PipeOutputTo(cat);
			
			
			if(cat.waitForOutput()){
				System.out.println("output: " + cat.getNormalOutput());
			}else{
				System.out.println("error: " + cat.getErrorOutput());				
			}
			
			System.out.println("\n \n end output");

		} catch (NonMatchingOSException | IOException | SProcessNotYetStartedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
