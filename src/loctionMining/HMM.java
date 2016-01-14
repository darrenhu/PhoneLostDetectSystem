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

public class HMM {
	public static Hmm<ObservationInteger> buildHmm(ArrayList<ArrayList<ObservationInteger>> entityLists,int numOfLocations, int numOfIterations){
	

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
	    // Initialize HMM.
        int numOfStates = 10;
        int numOfEntities = numOfLocations;
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
             learntHmm = bwl.iterate(learntHmm, entityLists);
//             System.out.println("Iteration "+i);
//            System.out.println(learntHmm.toString());
        }
        
        
//        System.out.println("Probability for 1: "+learntHmm.probability(entityLists.get(0)));
//        System.out.println("Probability for 2: "+learntHmm.probability(entityLists.get(1)));

        
//        System.out.println(learntHmm.getAij(0, 1));
        

        return learntHmm;
	}

}

