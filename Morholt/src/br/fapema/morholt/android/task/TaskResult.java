package br.fapema.morholt.android.task;

public class TaskResult {
	/**
	 * maybe not needed
	 */
	public Integer listPosition;
	public Long result;
	
	public TaskResult(Integer listPosition, Long result) {
		super();
		this.listPosition = listPosition;
		this.result = result;
	}
}
