package loctionMining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;

public class TempJava {
	public static Hmm<ObservationInteger> buildHmm(List<List<String>> entityLists,List<List<String>> testList, int numOfIterations){
        String fileName =
                "output_"
                        + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                                .format(new Date()) + ".txt";
        File f = new File(fileName);
        if (!f.exists()) {
            try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        final PrintStream ps = System.out; // Backup System.out
        // Set file as System.out
        try {
			System.setOut(new PrintStream(new FileOutputStream(f, true)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	    // Convert the input data into the form of HMM sequences.
	    Map<String,Integer> entity2Id = new HashMap<String,Integer>();
	    List<List<ObservationInteger>> sequences = new ArrayList<List<ObservationInteger>>();
	    for(List<String> entityList: entityLists){
	        List<ObservationInteger> seq = new ArrayList<ObservationInteger>();
	        for(String entity: entityList){
	            int entityId;
	            if(entity2Id.containsKey(entity)){
	                entityId = entity2Id.get(entity);
	            } else {
	                entityId = entity2Id.size();
	                entity2Id.put(entity, entityId);
	            }
	            seq.add(new ObservationInteger(entityId));
            }
	        sequences.add(seq);
	    }
/////////////////////////////////////////////////////////////////////////////////////////
	    List<List<ObservationInteger>> testSeq = new ArrayList<List<ObservationInteger>>();
	    for(List<String> entityList: testList){
	        List<ObservationInteger> seq = new ArrayList<ObservationInteger>();
	        for(String entity: entityList){
	            int entityId;
	            if(entity2Id.containsKey(entity)){
	                entityId = entity2Id.get(entity);
	            } else {
	                entityId = entity2Id.size();
	                entity2Id.put(entity, entityId);
	            }
	            seq.add(new ObservationInteger(entityId));
            }
	        testSeq.add(seq);
	    }
/////////////////////////////////////////////////////////////////////////////////////////
	    // Initialize HMM.
        int numOfStates = 10;
        int numOfEntities = entity2Id.size();
        Hmm<ObservationInteger> learntHmm = 
                  new Hmm<ObservationInteger>(numOfStates,
                          new OpdfIntegerFactory(numOfEntities));
        // Set initial distribution here.
        Random ran = new Random(1);
        for(int i = 0; i < numOfStates;i++){
        	learntHmm.setPi(i, ran.nextDouble()/2);	
        }
        double[] ranOpdf = new double[numOfEntities];
        for(int i = 0; i < numOfEntities; i++){
        	ranOpdf[i] = ran.nextDouble()/2;
        }
        for(int i = 0; i < numOfStates; i++){
        	learntHmm.setOpdf(i, new OpdfInteger(ranOpdf));
        }
        for(int i = 0; i < numOfStates;i++){
            for(int j = 0; j < numOfStates;j++){
            	learntHmm.setAij(i, j, ran.nextDouble()/2);
            }
        }
        
        // Baum-Welch learning.
        BaumWelchLearner bwl = new BaumWelchLearner();
        // Incrementally improve the solution.
        for (int i = 0; i < numOfIterations; i++) {
             learntHmm = bwl.iterate(learntHmm, sequences);
             System.out.println("Iteration "+i);
             System.out.println(learntHmm.toString());
        }
        
        System.out.println("Probability for 1: "+learntHmm.probability(sequences.get(0)));
        System.out.println("Probability for 2: "+learntHmm.probability(sequences.get(1)));
        System.out.println("Probability for 3: "+learntHmm.probability(testSeq.get(0)));
        System.out.println("Probability for 4: "+learntHmm.probability(testSeq.get(1)));
        
        System.out.println(learntHmm.getAij(0, 1));
        
        System.out.println("Print in file.");

        System.out.close(); // Close file
        System.setOut(ps); // Recover System.out
        System.out.println("Print in console");
        return learntHmm;
	}
}
