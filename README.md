# Shell-Command-Executor-Lib

This is a Java library that intends to make it easier and safer to execute shell commands on the operating system using Runtime.exec().
Note: It is probably advised to just use java's ProcessBuilder but this is a fun project to work on.


Installation
----------------------------------
1. Clone repository
2. run mvn install
3. Grab Shell-Command-Executor-Lib-?.?.?-SNAPSHOT.jar from the target folder
4. Add the jar to your project. (Using maven/eclipse/manually)



Usage
----------------------------------
To use this library simply make a subclass of the abstract "Command" class. This will force you to overwrite 2 methods that implement your custom command. The abstract Command superclass provides convenient methods for analyzing the result of the process.

**After creating the command object you can execute it using:**

    CommandExecutor exec = CommandExecutor.getCommandExecutor();
    Command customCommand = new YourCommand();
    exec.executeCommand(customCommand);

**A very simple example custom Command class:**

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

**A slightly more advanced custom Command class:**

	public class DirectoryListCommand extends Command{

		private final String command;
		private final OS osType;


		public DirectoryListCommand() {
			this.osType = CommandExecutor.getCommandExecutor().getOSType();
			
			if(this.osType == OS.UNIX){
				this.command = "ls";
			}else if(this.osType == OS.WINDOWS){
				this.command = "dir";
			}else{
				// Hoping echo at least exists, in real scenario, throw exception if required
				this.command = "echo OS type not supported";
			}
		}
		
		@Override
		public String getCommand() {
			return this.command;
		}

		@Override
		public OS getOSType() {
			return this.osType;
		}

	}
