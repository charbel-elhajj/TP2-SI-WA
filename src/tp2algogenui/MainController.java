/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp2algogenui;

import java.util.Calendar;
import javax.swing.JFrame;

/**
 *
 * @author Charbel
 */
public class MainController {
    private MainView view;
    
    public MainController(MainView view){
        this.view = view;
    }

    public void geneticAlgorithm(int popSize, int genesPerPop, float mutationRate, Crosstype crossType, boolean isVisual) {
        String output = "";
        Population pop = new Population(popSize, genesPerPop, crossType, mutationRate);
        Individual best = pop.getBestIndividual();
        int epoch=0;
        Calendar calendar = Calendar.getInstance();
        long timeMilli1 = calendar.getTimeInMillis();
        if(isVisual)
        {
            int optimalSolution = (int)Math.pow(2, genesPerPop) - 1;

            for(; epoch<50 && best.getFitnessScore()<optimalSolution; epoch++)
            {
                Population newPop = pop.generateNewPopulation();
                pop = newPop;
                best = pop.getBestIndividual();
            }
        }
        else
        {
            int convCounter=0;
            int lastBestFitness =  best.getFitnessScore();
            
            for(; epoch<50 && convCounter<5; epoch++)
            {
                Population newPop = pop.generateNewPopulation();
                pop = newPop;
                best = pop.getBestIndividual();
                if (best.getFitnessScore() == lastBestFitness){
                    convCounter++;
                }
                lastBestFitness = best.getFitnessScore();
            }
        }
        calendar = Calendar.getInstance();
        long timeMilli2 = calendar.getTimeInMillis();
        
        long executionTime = timeMilli2 - timeMilli1;
        output +="Meilleur Individu:\n"
                + ""+best+"\n"
                + "fitness = "+best.getFitnessScore()+"\n"
                + "Nb Epoch = "+epoch+"\n"
                + "Temps d'éxécution = "+executionTime+" ms";

        
        this.view.displayResult(output);

    }
    
    
    
}
