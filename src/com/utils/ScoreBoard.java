package com.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreBoard {
	public static HashMap<String, Unit> executionUnit = new HashMap<String,Unit>();
	public ArrayList<InstrnStats> dumpedInstruction = new ArrayList<InstrnStats>();
	static int m;
	static int loadCounter=0;
	static int multCounter=0;
	static int divCounter=0;
	static int aluCounter=0;
	public void addUnit(){
		executionUnit.put("Load",new Unit(2, "Load/Store Unit", "PipelinedVariable",true));
		executionUnit.put("ALU",new Unit(1, "ALU", "NonPipelined",true));
		executionUnit.put("Mult",new Unit(4, "Multiply", "Pipelined",true));
		executionUnit.put("Div",new Unit(8, "Divide", "NonPipelined",true));
	}
	public void issueInstruction(String instruction, int instructionNumber,int currentCycle,int cacheMissPenalty){
		System.out.println("\n>>>>>> At cycle: "+currentCycle+" Issued "+instruction+"\n");
		String instructiontype = instruction.split(" ")[0].split("-")[1];
		String destinationRegister = instruction.split(" ")[1];
		if(instructiontype.equalsIgnoreCase("LDM")||instructiontype.equalsIgnoreCase("LDH")||instructiontype.equalsIgnoreCase("ST")){
			Unit load = executionUnit.get("Load");
			if(instructiontype.equalsIgnoreCase("LDH"))
				loadCounter = load.getLatency();
			else if(instructiontype.equalsIgnoreCase("ST"))
				loadCounter = load.getLatency();
			else
				loadCounter = load.getLatency()+cacheMissPenalty;
			if(!instructiontype.equalsIgnoreCase("ST"))
				dumpedInstruction.add(new InstrnStats(instruction, destinationRegister, "Executing", loadCounter, instructionNumber, currentCycle));
		}
		else if(instructiontype.equalsIgnoreCase("MUL")){
			Unit mult = executionUnit.get("Mult");
			multCounter = mult.getLatency();
			dumpedInstruction.add(new InstrnStats(instruction, destinationRegister, "Executing", multCounter, instructionNumber, currentCycle));
		}
		else if(instructiontype.equalsIgnoreCase("DIV")){
			Unit div = executionUnit.get("Div");
			divCounter = div.getLatency();
			if(div.isAvailablity()){
				div.setAvailablity(false);
				dumpedInstruction.add(new InstrnStats(instruction, destinationRegister, "Executing", divCounter, instructionNumber, currentCycle));
			}
		}
		else{
			Unit alu = executionUnit.get("ALU");
			aluCounter = alu.getLatency();
			if(alu.isAvailablity()){
				alu.setAvailablity(false);
				dumpedInstruction.add(new InstrnStats(instruction, destinationRegister, "Executing", aluCounter, instructionNumber, currentCycle));	
			}
		}
		//System.out.println("Dumped Instruction: "+dumpedInstruction);
	}
	public int completionTime(Unit u,int currentCycle){
		int cyclesRemaining = u.getLatency();
		return cyclesRemaining;
	}
	public int advanceClock(int currentClock){
		for(int i=0;i<dumpedInstruction.size();i++){
			InstrnStats ins = dumpedInstruction.get(i);
			if(ins.getStatus().equalsIgnoreCase("complete")){
				System.out.println("\nCompleted Instruction: "+ins.getInstruction()+" at cycle "+currentClock+"\n");
				dumpedInstruction.remove(i);
			}
		}
		for(int i=0;i<dumpedInstruction.size();i++){
			InstrnStats ins = dumpedInstruction.get(i);
			int timeRemaining = ins.getCompletionCycles();
			if(timeRemaining == 0){
				ins.setStatus("Complete");
				String instrn = ins.getInstruction();
				String instructionType = instrn.split(" ")[0].split("-")[1];
				if(instructionType.equalsIgnoreCase("DIV")){
					Unit div = executionUnit.get("Div");
					div.setAvailablity(true);
				}
				else if(instructionType.equalsIgnoreCase("ADD") || instructionType.equalsIgnoreCase("SUB")){
					Unit div = executionUnit.get("ALU");
					div.setAvailablity(true);
				}	
			}
			else
				ins.setCompletionCycles(timeRemaining-1);
		}
		return currentClock+1;
	}
	public void dump(int currentClock){
		System.out.println("\nDump called at cycle: "+currentClock);
		for(int i=0;i<dumpedInstruction.size();i++){
			InstrnStats ins = dumpedInstruction.get(i);
			System.out.println("\n\t\t ScoreBoard instrn's: "+ins.getInstruction()+" "+ins.getDestinationRegister()+" "+ins.getStatus()+" "+ins.getCompletionCycles()+"\n");
		}
	}
	public boolean isCompleteCheck(){
		boolean check = false;
		for(int i=0;i<dumpedInstruction.size();i++){
			InstrnStats ins = dumpedInstruction.get(i);
			if(ins.getStatus().equalsIgnoreCase("complete"))
				check = true;
			else
				check = false;
		}
		return check;
	}
}
