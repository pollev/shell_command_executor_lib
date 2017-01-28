# Shell-Command-Executor-Lib

This is a Java library that intends to make it easier and safer to execute shell commands on the operating system using Runtime.exec()


Installation
----------------------------------
1. Clone repository
2. run mvn install
3. Grab Shell-Command-Executor-Lib-?.?.?-SNAPSHOT.jar from the target folder
4. Add the jar to your project. (Using maven/eclipse/manually)



Usage
----------------------------------
To use this library simply make a subclass of the abstract "Command" class. This will force you to overwrite 2 methods that implement your custom command.

After creating the command object you can execute it using::

    CommandExecutor exec = CommandExecutor.getCommandExecutor();
		Command customCommand = new YourCommand();
		exec.executeCommand(customCommand);

A very simple example custom Command class::

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


