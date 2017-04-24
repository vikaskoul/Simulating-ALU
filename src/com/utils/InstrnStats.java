package com.utils;

public class InstrnStats {
	String instruction;
	String destinationRegister;
	String status;
	int completionCycles;
	int instrnNumber;
	int startCycle;
	public int getInstrnNumber() {
		return instrnNumber;
	}
	public void setInstrnNumber(int instrnNumber) {
		this.instrnNumber = instrnNumber;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public String getDestinationRegister() {
		return destinationRegister;
	}
	public void setDestinationRegister(String destinationRegister) {
		this.destinationRegister = destinationRegister;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCompletionCycles() {
		return completionCycles;
	}
	public void setCompletionCycles(int completionCycles) {
		this.completionCycles = completionCycles;
	}
	public InstrnStats(String instruction, String destinationRegister,
			String status, int completionCycles, int instrnNumber,
			int startCycle) {
		super();
		this.instruction = instruction;
		this.destinationRegister = destinationRegister;
		this.status = status;
		this.completionCycles = completionCycles;
		this.instrnNumber = instrnNumber;
		this.startCycle = startCycle;
	}
	public int getStartCycle() {
		return startCycle;
	}
	public void setStartCycle(int startCycle) {
		this.startCycle = startCycle;
	}
}
