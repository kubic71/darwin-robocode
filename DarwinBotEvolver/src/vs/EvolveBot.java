package vs;

import org.jgap.*;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.MutationOperator;
import robocode.Rules;

public class EvolveBot {
	static int currentEpoch = 1;

	/*
	 * It's recommended to have pop_size = 10 * D, where D is the number of
	 * dimensions. Since we have only 4 parameters that have an effect on robots
	 * success, pop_size = 50 should be plenty
	 */
	static int POPULATION_SIZE = 150;
	static int EPOCHS_TOTAL = 1000;

	public static void main(String[] args) throws InvalidConfigurationException {
		FitnessFunction func = new RobotFitness();
		Configuration conf = new DefaultConfiguration();
		((BestChromosomesSelector) conf.getNaturalSelectors(false).get(0)).setOriginalRate(0.96);
		// We need more mutation
		((MutationOperator) conf.getGeneticOperators().get(1)).setMutationRate(3);
		conf.setFitnessFunction(func);

		// Create robot genes
		Gene[] sampleGenes = new Gene[4];

		sampleGenes[0] = new DoubleGene(conf, 0,
				Math.sqrt(Math.pow(RobotFitness.BATTLEFIELD_WIDTH, 2) + Math.pow(RobotFitness.BATTLEFIELD_HEIGHT, 2))); // distanceLimit
		sampleGenes[1] = new DoubleGene(conf, 0, 1); // change speed probability
		sampleGenes[2] = new DoubleGene(conf, 0, Rules.MAX_VELOCITY); // speed range
		sampleGenes[3] = new DoubleGene(conf, 0, Rules.MAX_VELOCITY); // min speed

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
				for (Object best : population.getFittestChromosomes(5)) {
					IChromosome chr = (IChromosome) best;
					System.out.printf("%.2f\t%.2f\t%.5f\t%.2f\t%.2f\n", chr.getFitnessValue(),
							getGeneVal(chr.getGene(0)), getGeneVal(chr.getGene(1)), getGeneVal(chr.getGene(2)),
							getGeneVal(chr.getGene(3)));
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
