package com.codeablereason.hashexperiments.hashfunctions;

import com.codeablereason.hashexperiments.LongHashFunction;

/**
 * Default hash in java for longs
 */
public class JavaLongHash implements LongHashFunction {
    @Override
    public int hash(long input) {
        return new Long(input).hashCode();
    }
}
