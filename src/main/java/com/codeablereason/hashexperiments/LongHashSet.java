package com.codeablereason.hashexperiments;

import com.codeablereason.hashexperiments.hashfunctions.Md5Hash;
import com.codeablereason.hashexperiments.hashfunctions.Murmur3Hash;

/**
 * Minimal HashSet for Longs, using open addressing with linear probing and pluggable hash function.
 * Used in proof of concept for HashShuffle
 *
 * Limitations: does only what it needs to for HashShuffle algorithm and nothing more:
 *  - Only supports storing positive longs (because -1 is used to denote a free slot)
 *  - Highly thread-unsafe, and iterators may break if Set is modified after creation
 *
 * @author Sam Van Oort
 */
public class LongHashSet {
    public static final long EMPTY_SLOT = -1;

    /** Masks out top bit ONLY to remove negative numbers... not the same as doing absolute value though! */
    public static long TOP_BIT_MASK = 0x7FFFFFFFFFFFFFFFL;
    long[] table;

    int elementCount = 0;
    double loadFactor = 0.5;

    LongHashFunction hashFunction = new Md5Hash();

    public LongHashSet(){
        this.table = initTable(17);
    }


    /** Create a LongHashSet pre-allocated to store numElements elements*/
    public LongHashSet(int numElements) {
        int count = (int)(((double)numElements)/loadFactor) + 1;
        this.table = initTable(count);
    }

    public LongHashSet(int numElements, LongHashFunction hashFunction) {
        int count = (int)(((double)numElements)/loadFactor) + 1;
        this.table = initTable(count);
        this.hashFunction = hashFunction;
    }

    /** Generate a table of specified size */
    long[] initTable(int numElements) {
        long[] array = new long[numElements];
        java.util.Arrays.fill(array,EMPTY_SLOT);
        return array;
    }

    /** Store an input long */
    public void add(long input) {
        if (input < 0) {
            throw new IllegalArgumentException("Negative numbers not allowed, found: "+input);
        }

        int slot = findSlot(input, table);
        if (slot != -1 ) { //Already in set
            table[slot] = input;
            elementCount++;
        }

        //Resize the hashtable if it is getting too full
        //If we use a robust hash, a prime size does not offer any benefits
        if (((double) elementCount)/(double)(table.length) >= loadFactor) {
            resize(Math.min(Integer.MAX_VALUE,table.length * 2 + 1));
        }
    }

    /** Tries to find the first empty slot to store the given long in table, or -1 if already present */
    int findSlot(long input, long[] table) {
        int slot = Math.abs(hashFunction.hash(input) % table.length);
        long slotValue = table[slot];

        //Try the slot itself before linear probing
        if (slotValue == EMPTY_SLOT) {
            return slot;
        } else if (slotValue == input) {
            return -1;
        }

        //Linear probing through hash table slots
        int length = table.length;
        while (true) { //Not endless loop because table is never full
            slot = (slot+1) % length;
            slotValue = table[slot];

            if (slotValue == EMPTY_SLOT) {
                return slot;
            } else if (slotValue == input) {
                return -1;
            }
        }

    }

    public int size() {
        return this.elementCount;
    }

    public boolean contains(long item) {
        return (item >= 0 && findSlot(item,table) == -1);
    }

    //Resize HashSet to accommodate additional slots
    void resize(int size) {
        long[] newTable = initTable(size);

        //Store all values to new table
        for (int i=0; i < table.length; i++) {
            long value = table[i];
            if (value != EMPTY_SLOT) {
                int slot = findSlot(value, newTable);
                newTable[slot] = value;
            }
        }
        table = newTable;
    }

    public LongHashFunction getHashFunction() {
        return this.hashFunction;
    }

    public void setHashFunction(LongHashFunction func) {
       this.hashFunction = func;
       resize(this.table.length); //Forces rehashing using new hash function
    }

    /** Not even a LITTLE bit modification-safe */
    public static class LongIterator {
        long[] table;

        /** Array index */
        int index = -1;

        /** Item index */
        int elementNumber = 0;

        /** Number of elements in hashtable */
        int elementCount;

        public boolean hasNext() {
            return elementNumber < elementCount;
        }

        /** Iterate and return the next long */
        public long nextLong() {
            if (!hasNext()) {
                throw new RuntimeException("Tried to iterate when no values left!");
            }
            index++;
            while(index < table.length && table[index] == EMPTY_SLOT) {
                index++;
            }
            elementNumber++;
            return table[index];
        }

        //Create iterator over given hashset
        LongIterator(LongHashSet set) {
            this.table = set.table;
            this.elementCount = set.elementCount;
        }
    }


    /** Returns an iterator over the stored longs */
    public LongIterator iterator() {
        return new LongIterator(this);
    }
}
