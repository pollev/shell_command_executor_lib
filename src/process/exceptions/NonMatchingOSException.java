package process.exceptions;

import process.SProcessExecutor;

public class NonMatchingOSException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NonMatchingOSException(SProcessExecutor.OS executorType, SProcessExecutor.OS commandType){
		super("Mismatch between executor type (" + executorType + ") and SProcess type (" + commandType + ").");
	}

}
