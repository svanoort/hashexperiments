package com.codeablereason.hashexperiments;

import java.util.Arrays;
import java.util.Random;

/**
 * Implementation of shuffle algorithm based on hashing
 */
public class HashShuffle {

    public static long LONG_HALF_ONES = 0xFFFFFFFFL;

    /** Shuffle an object array using index shuffle */
    //TODO generify
    public static Object[] shuffle(Object[] input) {
        //Get new indices for each array slot
        int[] indices = getShuffledIndices(input.length);
        Object[] output = new Object[input.length];

        //Set output indices to the shuffled indices
        for(int i=0; i<indices.length; i++) {
            int index = indices[i];
            output[index] = input[i];
        }
        return output;
    }

    /** Shuffle inputs using an index shuffle array */
    public static int[] shuffle(int[] input) {
        //Get new indices for each array slot
        int[] indices = getShuffledIndices(input.length);
        int[] output = new int[input.length];

        //Set output indices to the shuffled indices
        for(int i=0; i<indices.length; i++) {
            int index = indices[i];
            output[index] = input[i];
        }
        return output;
    }

    /** Pack two int into a single long */
    public static long packInts(int upperBits, int lowerBits) {
        return  ((upperBits & LONG_HALF_ONES) << 32) | (lowerBits & LONG_HALF_ONES);
    }

    public static int lowerHalf(long input) {
        return (int)input;
    }

    //Shuffle indices (which are all unique and greater than zero)
    public static int[] getShuffledIndices(int length) throws IllegalArgumentException {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length, must be greater than 0: "+length);
        }
        //Below generates a random number, so each shuffle is unique
        //Technically, with a robust hash function you could get unique ordering with just nanoTime value
        int random = new java.util.Random((int)(System.nanoTime())).nextInt();
        long shifted = ((random & 0xFFFFFFFFFFFFFFFFL) << 32) & LongHashSet.TOP_BIT_MASK; //Set top value to always be 1
        LongHashSet set = new LongHashSet(length); //Create a LongHashSet

        //Add to hashset
        for (int i=0; i<length; i++) {
            //Top bit always 0, next 31 are random, bottom 32 bits are from input
            long value = packInts(random,i) & LongHashSet.TOP_BIT_MASK;

            //Below does the magic: the hash function will distribute inputs over array slots
            //By using the random number above in the hash function, the slot something arrives at is now random
            set.add(value);
        }

        int[] output = new int[length];
        int index = 0;
        LongHashSet.LongIterator it = set.iterator(); //The input values are now randomly distributed
        while (it.hasNext()) {
            output[index++] = (int)(it.nextLong()); //Bottom 32 bits are the inputs
        }
        return  output;
    }

    /** Return an output containing the average value for each slot in a shuffled array */
    public static double[] averageShuffleSlot(int numElements, int numPasses) {
        Random rand = new Random(System.nanoTime());

        double[] means = new double[numElements]; //Holds mean value


        for(int i=0; i< numPasses; i++) {
            int[] shuffled = HashShuffle.getShuffledIndices(numElements);
            for(int j=0; j<numElements; j++) {
                means[j] += ((double)shuffled[j])/((double)numPasses);
            }
        }
        return means;
    }

    /** Test and display unusual average values in shuffle index array, indicating nonrandom behaviour  */
    public static void findUsualAverages(int numElements, int passes) {
        double expected =((double)numElements-1.0) / 2.0;
        double[] means = averageShuffleSlot(numElements, passes);

        for(int i=0; i<numElements; i++) {
            double ratio = means[i]/expected;
            if (ratio > 1.01 || ratio < 0.99) { //1% deviation from randomness
                System.out.println("Unusual average in slot: "+i+" ratio of expected to real average: "+ratio);
            }
        }
    }

    public static void main(String[] args) {
        int[] indices = HashShuffle.getShuffledIndices(32);
        System.out.println("Sample Shuffled 32 elements: ");
        System.out.println(java.util.Arrays.toString(indices));



        //Below will take a while to run, but finds elements where the average value in a slot
        //Differs from what would be expected for random behaviour
        System.out.println("STARTING Testing statistical randomness of the shuffle functions.");
        findUsualAverages(100,500000);
        System.out.println("DONE Testing statistical randomness of the shuffle functions, any anomalies will be above.");
    }
}
