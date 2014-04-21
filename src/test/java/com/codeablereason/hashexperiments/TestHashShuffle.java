package com.codeablereason.hashexperiments;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by svanoort on 4/20/14.
 */
public class TestHashShuffle extends TestCase {

    @Test
    public void testShuffle() {
        int[] shuffled = HashShuffle.getShuffledIndices(1);
        assertEquals(1,shuffled.length);
        assertEquals(0, shuffled[0]);

        int count = 15;
        shuffled = HashShuffle.getShuffledIndices(count);
        assertEquals(count,shuffled.length);
        testIndices(shuffled,count);

        count = 150;
        shuffled = HashShuffle.getShuffledIndices(count);
        assertEquals(count,shuffled.length);
        testIndices(shuffled,count);
    }

    @Test
    public void testObjectShuffle() { //Same algorithm used for int array, just type changed
        Random rand = new Random(System.nanoTime());
        HashSet<Double> expected = new HashSet<Double>();
        Double[] objects = new Double[1000];
        for (int i=0; i<1000; i++) {
            double dub = rand.nextDouble();
            expected.add(dub);
            objects[i]=new Double(dub);
        }

        //Shuffle and bulk-cast array.  Goes away if you generify this
        Object[] uncast = HashShuffle.shuffle(objects);
        Double[] shuffled = Arrays.copyOf(uncast, uncast.length, Double[].class);


        assertEquals(1000,shuffled.length);
        for(Double d : shuffled) {
            assertTrue("Found an element not in expected output: "+d, expected.contains(d));
        }

        Arrays.sort(objects);
        Arrays.sort(shuffled);
        Arrays.deepEquals(objects,shuffled);
    }

    @Test
    public void testBitFiddling() {
        long packed = HashShuffle.packInts(9,-78);
        assertTrue("Negative packed in where positive expected: " + packed, packed > 0);
        int unpacked = HashShuffle.lowerHalf(packed);
        assertEquals(-78,unpacked);
    }

    /** Test that the shuffled array contains all input integers up to length elements */
    private void testIndices(int[] shuffled, int length) {
        //Add the input array to a HashSet
        HashSet<Integer> ints = new HashSet<Integer>(shuffled.length);
        for(int i=0; i<shuffled.length; i++) {
            ints.add(new Integer(shuffled[i]));
        }

        //Change that range up to length -1 is in the hashset
        for(int i=0; i<length; i++) {
            assertTrue("Missing index: "+i, ints.contains(new Integer(i)));
        }
    }

}
