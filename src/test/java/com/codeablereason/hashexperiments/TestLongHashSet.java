package com.codeablereason.hashexperiments;

import com.codeablereason.hashexperiments.LongHashSet;
import junit.framework.TestCase;
import org.junit.Test;
import junit.framework.Assert;

import java.util.HashSet;
import java.util.Random;

public class TestLongHashSet extends TestCase {

    @Test
    public void testInitializeSet() {
        long[] initialized = new LongHashSet().initTable(32);
        assertEquals(32,initialized.length);
        for(int i=0; i<initialized.length; i++){
            assertEquals(LongHashSet.EMPTY_SLOT,initialized[i]);
        }
    }

    @Test
    public void testFindSlot() {
        LongHashSet set = new LongHashSet();
        int slot = set.findSlot(0,set.table);
        assert(slot != -1);
    }

    @Test
    public void testStoringSingleValue() {
        LongHashSet set = new LongHashSet();
        set.add(0);
        assert(set.contains(0));

        //Test with various random positive ints
        java.util.Random rand = new Random(System.nanoTime());
        for(int i=0; i<8000; i++) {
            long randomVal = (rand.nextLong() & LongHashSet.TOP_BIT_MASK);
            set = new LongHashSet();
            set.add(randomVal);
            assert(set.contains(randomVal));
        }
    }

    @Test
    public void testStoringManyRandomValues() {
        int maxValues = 500;
        int tests = 1000;
        java.util.Random rand = new Random(System.nanoTime());

        //Test numerous times with random sizes of sets
        for(int i=0; i<tests; i++) {
            int size = Math.abs(rand.nextInt(maxValues));
            HashSet<Long> expected = new HashSet<Long>(size);
            LongHashSet set = new LongHashSet(size);
            for(int j = 0; j<size; j++) {
                long newRand = rand.nextLong() & LongHashSet.TOP_BIT_MASK;
                expected.add(new Long(newRand));
                set.add(newRand);
                compareSets(expected, set);
            }
        }

    }

    /** Add ranges */
    void addRange(LongHashSet set, long start, long end) {
        for (long i=start; i<end; i++) {
            set.add(i);
        }
    }

    /** Test sets match */
    void compareSets(HashSet<Long> expected, LongHashSet stored) {
        HashSet<Long> dupe = new HashSet<Long>();
        LongHashSet.LongIterator it = stored.iterator();
        while(it.hasNext()) {
            dupe.add(new Long(it.nextLong()));
        }

        //Test sizes match
        assertEquals(stored.size(),dupe.size());
        assertEquals(expected.size(),dupe.size());
        assertTrue("LongSet contains items not expected: ",expected.containsAll(dupe));
        assertTrue("Expected set contains items not in output: ", dupe.containsAll(expected));
    }



}
