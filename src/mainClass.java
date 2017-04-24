import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.utils.InstrnStats;
import com.utils.ScoreBoard;


public class mainClass {

	/**
	 * @param args
	 */
	public static ArrayList<String> fileData = new ArrayList<String>();
	public static ArrayList<String> instrnData = new ArrayList<String>();
	public static ArrayList<String> issueQueue = new ArrayList<String>();
	public static HashMap<String, Boolean> globalDependencyTracker = new HashMap<String,Boolean>();
	public static String dumpStats = "";
	public static int ISSUEWIDTHSIZE = 0;
	public static int CACHEMISS = 0;
	public static int cycle=0;
	public static int currentInstructionNumber = 0;
	public static int currentGlobalInstructionCounter = 0;
	public static String prevInstrn = "";
	public static void dispatchInstruction(String instrn,int cycle){
		if(currentGlobalInstructionCounter != instrnData.size()){
			issueQueue.add(instrn);
			System.out.println("\n>> At cycle: "+cycle+" Dispatch "+instrn+"\n");
		}
	}
	public static boolean getCycleCheck(ScoreBoard s,int currentCycle,String currentInstruction){
		boolean val = false;
		String c_instrn = currentInstruction.split(" ")[0].split("-")[1];
		for(int i=0;i<s.dumpedInstruction.size();i++){
			InstrnStats ins = s.dumpedInstruction.get(i);
			if(ins.getStartCycle() == currentCycle && ins.getStatus().equalsIgnoreCase("Executing")){			
				if(ins.getInstruction().split(" ")[0].split("-")[1].equalsIgnoreCase("LDM") && c_instrn.equalsIgnoreCase("LDH"))
					val = true;
				if(ins.getInstruction().split(" ")[0].split("-")[1].equalsIgnoreCase("LDH") && c_instrn.equalsIgnoreCase("LDM"))
					val = true;
				if(ins.getInstruction().split(" ")[0].split("-")[1].equalsIgnoreCase(c_instrn))
					val = true;
			}
		}		
		return val;
	}
	public static void issueInstruction(ScoreBoard s,int cycle){
		int iWidth=0;
		for(int i=0;i<s.dumpedInstruction.size();i++){
			InstrnStats ins = s.dumpedInstruction.get(i);
			if(ins.getStatus().equalsIgnoreCase("complete")){			
				//Move code from here to main Class as this is causing prob before issuing the next instruction
				if(globalDependencyTracker.containsKey(ins.getDestinationRegister())){
					globalDependencyTracker.put(ins.getDestinationRegister(),false);
				}
			}
		}
//		if(cycle == 1)
//			System.out.println("here for check");
		while(iWidth < ISSUEWIDTHSIZE){
			if(currentInstructionNumber < issueQueue.size()){
				String instrn =  issueQueue.get(currentInstructionNumber);
				int instructionNumber = Integer.parseInt(instrn.split(" ")[0].split("-")[0]);
				String dest = instrn.split(" ")[1];
				if(currentInstructionNumber == 0){
					globalDependencyTracker.put(dest, true);
					s.issueInstruction(instrn, instructionNumber, cycle,CACHEMISS);
					currentInstructionNumber++;	
					prevInstrn = instrn;
				}
				else{
					boolean unitCheck = getCycleCheck(s,cycle,instrn);
					//System.out.println("unitCheck val: "+unitCheck+" for "+instrn+" at cycle "+cycle);
					if(!unitCheck){
						boolean check = checkDependency(instrn);
						boolean getCompletionStatus = getCompletionStatus(s,prevInstrn);
						if(!getCompletionStatus){
							boolean falseCheck = checkAntiDependency(prevInstrn,instrn);
							if(!check && !falseCheck){
								globalDependencyTracker.put(dest, true);
								s.issueInstruction(instrn, instructionNumber, cycle,CACHEMISS);		
								prevInstrn = instrn;
								currentInstructionNumber++;	
							}
						}
						else{
							globalDependencyTracker.put(dest, true);
							s.issueInstruction(instrn, instructionNumber, cycle,CACHEMISS);		
							prevInstrn = instrn;
							currentInstructionNumber++;	
						}
					}
				}
			}
			iWidth++;
		}
	}
	private static boolean getCompletionStatus(ScoreBoard s, String prevInstrn2) {
		// TODO Auto-generated method stub
		boolean chkVal = false;
		for(int i=0;i<s.dumpedInstruction.size();i++){
			InstrnStats ins = s.dumpedInstruction.get(i);
			if(ins.getInstruction().equalsIgnoreCase(prevInstrn2) && ins.getStatus().equalsIgnoreCase("complete")){
				chkVal = true;
			}
		}
		return chkVal;
	}
	private static boolean checkAntiDependency(String prevInstrn2, String instrn) {
		// TODO Auto-generated method stub
		String old_src1 = prevInstrn2.split(" ")[2];
		String old_src2 = prevInstrn2.split(" ")[3];
		String new_dest = instrn.split(" ")[1];
		String old_dest = prevInstrn2.split(" ")[1];
		boolean checkVal =false;
		if(new_dest.equalsIgnoreCase(old_src1) || new_dest.equalsIgnoreCase(old_src2))
			checkVal=true;
		if(new_dest.equalsIgnoreCase(old_dest))
			checkVal=true;
		return checkVal;
	}
	private static boolean checkDependency(String instrn) {
		boolean returnVal,returnVal1 = false,returnVal2 = false;
		String src1 = instrn.split(" ")[2];
		String src2 = instrn.split(" ")[3];
		if(globalDependencyTracker.containsKey(src1)){
			returnVal1 = globalDependencyTracker.get(src1);
		}
		if(globalDependencyTracker.containsKey(src2)){
			returnVal2 = globalDependencyTracker.get(src2);
		}
		returnVal = returnVal1 || returnVal2;
		return returnVal;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		ScoreBoard scoreboard = new ScoreBoard();
		System.out.println("\n\n ************Problem 1 - InOrder Execution Start****************\n");
		try {
			String line=null;
			br = new BufferedReader(new FileReader(args[0]));
			//Saving all the instructions in the issue queue
			while ((line = br.readLine()) != null) {
				fileData.add(line);
			}
			//Getting the basic params needed for execution from the instruction
			for (int i = 0; i < fileData.size(); i++) {
				String data = fileData.get(i).split(" ")[0];
				if(data.equalsIgnoreCase("DUMP")){
					dumpStats+=fileData.get(i);
					fileData.remove(i);
					i--;
				}
				if(data.equalsIgnoreCase("ISSUEWIDTH")){
					ISSUEWIDTHSIZE=Integer.parseInt(fileData.get(i).split(" ")[1]);
					fileData.remove(i);
					i--;
				}
				if(data.equalsIgnoreCase("CACHEMISS")){
					CACHEMISS=Integer.parseInt(fileData.get(i).split(" ")[1]);
					fileData.remove(i);
				}
			}
			//Setting instruction numbers
			for (int i = 0; i < fileData.size(); i++) {
				instrnData.add(i+"-"+fileData.get(i));
			}
			//Generating the Units using addUnit function of scoreboard
			scoreboard.addUnit();
			while(true){
				String instruction = null;
				if(currentGlobalInstructionCounter < instrnData.size()){
					instruction = instrnData.get(currentGlobalInstructionCounter);
					if(cycle == 0){
						dispatchInstruction(instruction,cycle);
					}
					else{
						if(!instruction.equalsIgnoreCase(null))
							dispatchInstruction(instruction,cycle);
						issueInstruction(scoreboard,cycle);
					}
				}
				else
					issueInstruction(scoreboard,cycle);
				String dumpPrint[] = dumpStats.split(" ");
				for(int j=1;j<dumpPrint.length;j++){
					if(cycle == Integer.parseInt(dumpPrint[j])){
						scoreboard.dump(cycle);
					}
				}
				cycle = scoreboard.advanceClock(cycle);
				currentGlobalInstructionCounter++;
				if(scoreboard.isCompleteCheck() && instrnData.size() == currentInstructionNumber)
					break;
			}
			System.out.println("\n\n ************Problem 2 - Out of Order Execution Start****************\n");
			OutOfOrderProcessing oop = new OutOfOrderProcessing();
			oop.outOfOrderExecution(args[0]);
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally 
		{
			try {
				if (br != null){
					br.close();
				}
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}	
	}
}