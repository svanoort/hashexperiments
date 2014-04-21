package com.codeablereason.hashexperiments.hashfunctions;

import com.codeablereason.hashexperiments.LongHashFunction;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/** MD5 based hashing */
public class Md5Hash implements LongHashFunction {
    HashFunction func = Hashing.md5();

    @Override
    public int hash(long input) {
        return func.hashLong(input).asInt();
    }
}
