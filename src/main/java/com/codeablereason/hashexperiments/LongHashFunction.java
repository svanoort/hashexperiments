package com.codeablereason.hashexperiments;

/**
 * Pluggable hash function that hashes long integer to normal integer
 *
 * Suggestions:
 * http://research.neustar.biz/2012/02/02/choosing-a-good-hash-function-part-3/
 * - City Hash
 * - FNV-1A hash, murmur3 hash, spooky hash
 */
public interface LongHashFunction {
    public int hash(long input);
}
