package vs;

import org.jgap.*;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.event.IEventManager;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;
import robocode.Rules;

import java.util.ArrayList;

public class EvolveBot {
    static int currentEpoch = 1;

    /*
     * It's recommended to have pop_size = 10 * D, where D is the number of dimensions.
     * Since we have only 4 parameters that have an effect on robots success, pop_size = 50 should be plenty
     */
    static int POPULATION_SIZE = 500;
    static int EPOCHS_TOTAL = 100000;


    public static void main(String[] args) throws InvalidConfigurationException {
        FitnessFunction func = new RobotFitness();
        Configuration conf = new DefaultConfiguration();

        // We need more mutation
        MutationOperator mutOp = new MutationOperator(conf);
        mutOp.setMutationRate(6);
        conf.addGeneticOperator(mutOp);

        conf.setFitnessFunction(func);

        // Create robot genes
        Gene[] sampleGenes = new Gene[4 + ParameterizedSuperTracker.EVOLVED_COLORS_NUM];

        sampleGenes[0] = new DoubleGene(conf, 0, Math.sqrt(Math.pow(RobotFitness.BATTLEFIELD_WIDTH, 2) + Math.pow(RobotFitness.BATTLEFIELD_HEIGHT, 2))); // distanceLimit
        sampleGenes[1] = new DoubleGene(conf, 0, 1); // change speed probability
        sampleGenes[2] = new DoubleGene(conf, 0, 3*Rules.MAX_VELOCITY); // speed range
        sampleGenes[3] = new DoubleGene(conf, 0, 3*Rules.MAX_VELOCITY); // min speed


        for (int i = 0; i < ParameterizedSuperTracker.EVOLVED_COLORS_NUM; i++) {
            sampleGenes[i + 4] = new DoubleGene(conf, 0, 1); // HSV-color
        }

        Chromosome sampleChromosome = new Chromosome(conf);
        sampleChromosome.setGenes(sampleGenes);
        conf.setSampleChromosome(sampleChromosome);


        conf.setPopulationSize(POPULATION_SIZE);
        Genotype population = Genotype.randomInitialGenotype(conf);

        conf.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
            @Override
            public void geneticEventFired(GeneticEvent geneticEvent) {
                System.out.println("\nEpoch:" + Integer.toString(currentEpoch));
                System.out.println("fitness\tdist\tv_prob\tv_range\tmin_v");
                for(Object best : population.getFittestChromosomes(5)) {
                    IChromosome chr = (IChromosome)best;
                    System.out.printf("%.2f\t%.2f\t%.5f\t%.2f\t%.2f\n", func.getFitnessValue(chr), getGeneVal(chr.getGene(0)), getGeneVal(chr.getGene(1)), getGeneVal(chr.getGene(2)), getGeneVal(chr.getGene(3)));
                }

                currentEpoch++;
            }
        });

        population.evolve(EPOCHS_TOTAL);
    }



    private static double getGeneVal(Gene gene) {
        return ((DoubleGene) gene).doubleValue();
    }


    public static void printChromosome(IChromosome chromosome) {
        Gene[] genes = chromosome.getGenes();

        System.out.println("Dist_limit:" + genes[0].getPersistentRepresentation());
        System.out.println("change_speed_prob:" + genes[1].getPersistentRepresentation());
        System.out.println("speed_range:" + genes[2].getPersistentRepresentation());
        System.out.println("min_speed:" + genes[3].getPersistentRepresentation());
    }
}

