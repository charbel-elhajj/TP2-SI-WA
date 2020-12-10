package tp2algogenui;


import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Population {

    private Individual[] individuals;
    private int genesPerPop;
    private Crosstype crosstype;
    private float mutationChance;
    private int tournoiPosition = 0;

    /**
     * Representation of a population of pseudo-randomly generated individuals
     * @param popSize set the size of this population
     * @param genesPerPop sets the gene size of each individual in the pool
     * @param crosstype the crosstype to be used by this population
     * @param mutationChance chance for an individual to mutate at birth
     */
    public Population(int popSize, int genesPerPop, Crosstype crosstype, float mutationChance)
    {
        this.individuals = new Individual[popSize];
        this.genesPerPop = genesPerPop;
        this.crosstype = crosstype;
        this.mutationChance = mutationChance;
        for(int i=0; i<popSize; i++)
            this.individuals[i] = new Individual(genesPerPop);
    }

    /**
     * Representation of a population of pre-computed individuals
     * @param individuals an array of individuals
     * @param crosstype the crosstype to be used by this population
     * @param mutationChance chance for an individual to mutate at birth
     */
    public Population(Individual[] individuals, Crosstype crosstype, float mutationChance)
    {
        assert individuals.length > 0;
        this.individuals = individuals;
        this.genesPerPop = individuals[0].getGenes().length;
        this.crosstype = crosstype;
        this.mutationChance = mutationChance;
    }

    /**
     * Creates a new population using this generation's individuals
     * @return the newly generated population
     */
    public Population generateNewPopulation() 
    {
        Individual[] newIndividuals = new Individual[this.individuals.length];
        int somme = calculSomme();
        Random rand = new Random();
        int crosspoint;
        for(int i=0; i<individuals.length; i+=2){
            Individual parent1;
            Individual parent2;
            if (this.crosstype == Crosstype.ROULETTE){
                parent1 = rouletteSelection(somme);
                parent2 = rouletteSelection(somme);
                
            }
            else{
                parent1 = tournoiSelection();
                parent2 = tournoiSelection();
            }
            crosspoint = rand.nextInt(parent1.getGenes().length);
            Individual[] children = reproduceIndividuals(parent1,parent2,crosspoint);
            children = mutateChildren(children);
            newIndividuals[i] = children[0];
            newIndividuals[i+1] = children[1];
        }
        return new Population(newIndividuals, this.crosstype, this.mutationChance);
        
    }
    
    private Individual[] mutateChildren(Individual[] children){
        
        Random rand = new Random();
        float prob = rand.nextFloat();
        int index;
        if(prob<this.mutationChance){
            index = rand.nextInt(children[0].getGenes().length);
            children[0].geneFlip(index);
        }
        prob = rand.nextFloat();
        if(prob<this.mutationChance){
            index = rand.nextInt(children[1].getGenes().length);
            children[1].geneFlip(index);
        }
        return children;
    }
    
    private int calculSomme(){
        int somme = 0;
        for(Individual individual : individuals){
            somme += individual.getFitnessScore();
        }
        return somme;
    }
    private Individual rouletteSelection(int somme){
        Random rand = new Random();
        int probability = rand.nextInt(somme);
        int cumul = 0;
        int index = 0;
        while(cumul + this.individuals[index].getFitnessScore() < probability){
            cumul += this.individuals[index].getFitnessScore();
            index++;
        }
        return this.individuals[index];
    }

    /**
     * Takes 2 individuals and create 2 children using their genes
     * @param firstParent the first selected individual
     * @param secondParent the second selected individual
     * @param crosspoint index of the crosspoint
     * @return an array of 2 individuals
     */
    public Individual[] reproduceIndividuals(Individual firstParent, Individual secondParent, int crosspoint)
    {
        Individual[] offsprings = new Individual[2];
        
        int[] firstChildGenes = new int[genesPerPop];
        int[] secondChildGenes = new int[genesPerPop];
        for(int i=0; i<crosspoint; i++){
            firstChildGenes[i] = firstParent.getGenes()[i];
            secondChildGenes[i] = secondParent.getGenes()[i];
        }
        for(int i=crosspoint; i<firstParent.getGenes().length; i++){
            secondChildGenes[i] = firstParent.getGenes()[i];
            firstChildGenes[i] = secondParent.getGenes()[i];
        }
        offsprings[0] = new Individual(firstChildGenes);
        offsprings[1] = new Individual(secondChildGenes);
        return offsprings;

    }
    
    public Individual getBestIndividual(){
        int index = 0;
        for(int i =1;i<individuals.length;i++){
            if(individuals[i].getFitnessScore()>individuals[index].getFitnessScore()){
                index = i;
            }
        }
        
        return individuals[index];
    }
    
    private void melangePopulation(int nbMelange){
        Random rand = new Random();
        Individual tmp;
        int index1, index2;
        for(int i =0;i<nbMelange;i++){
            index1 = rand.nextInt(this.individuals.length);
            index2 = rand.nextInt(this.individuals.length);
            tmp = individuals[index1];
            individuals[index1] = individuals[index2];
            individuals[index2] = tmp;
        }
    }
    
    private Individual tournoiSelection(){
        if(individuals.length - tournoiPosition < 2){
            melangePopulation(10);
            tournoiPosition = 0;
        }
        Individual candidat1 = individuals[tournoiPosition];
        Individual candidat2 = individuals[tournoiPosition+1];
        tournoiPosition += 2;
        if(candidat1.getFitnessScore()>candidat2.getFitnessScore()){
            return candidat1;
        }
        else{
            return candidat2;
        }
    }

    @Override
    public String toString()
    {
        return "Population{" +
                "individuals=" + Arrays.toString(individuals) +
                ", genesPerPop=" + genesPerPop +
                '}';
    }
}
