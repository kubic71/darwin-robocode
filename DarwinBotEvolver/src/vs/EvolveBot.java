package vs;

import org.jgap.*;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

public class EvolveBot {


    public static void main(String[] args) throws InvalidConfigurationException {
        FitnessFunction func = new RobotFitness();
        Configuration conf = new DefaultConfiguration();
        conf.setFitnessFunction(func);
        Gene[] sampleGenes = new Gene[4];

        sampleGenes[0] = new DoubleGene(conf, 0, 1000); // distanceLimit
        sampleGenes[1] = new DoubleGene(conf, 0, 1000); // change speed probability
        sampleGenes[2] = new DoubleGene(conf, 0, 100); // speed range
        sampleGenes[3] =  new DoubleGene(conf, 0, 50); // min speed


        Chromosome sampleChromosome = new Chromosome(conf);
        sampleChromosome.setGenes(sampleGenes);

        conf.setSampleChromosome(sampleChromosome);

        conf.setPopulationSize(5);
        Genotype population = Genotype.randomInitialGenotype( conf );

        population.evolve(5);

        Chromosome bestSoFar = (Chromosome) population.getFittestChromosome();
        System.out.println("Best chromosome:");
        printChromosome(bestSoFar);
    }

    public static void printChromosome(IChromosome chromosome) {
        Gene[] genes = chromosome.getGenes();
        System.out.println("Dist_limit:" + genes[0].getPersistentRepresentation());
        System.out.println("change_speed_prob:" + genes[1].getPersistentRepresentation());
        System.out.println("speed_range:" + genes[2].getPersistentRepresentation());
        System.out.println("min_speed:" + genes[3].getPersistentRepresentation());
        }
    }

