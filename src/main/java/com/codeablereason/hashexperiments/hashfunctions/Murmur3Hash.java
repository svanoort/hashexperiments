package com.codeablereason.hashexperiments.hashfunctions;

import com.codeablereason.hashexperiments.LongHashFunction;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/** Basic murmur3 hash */
public class Murmur3Hash implements LongHashFunction {
    HashFunction func = Hashing.murmur3_32();

    @Override
    public int hash(long input) {
        return func.hashLong(input).hashCode();
    }


}
