hashexperiments
===============

hashexperiments - how do you shuffle an array if you've never had to do a shuffle before?  Or, "what's the craziest way to implement a shuffle... which actually works!"

The standard approach to shuffle algorithms is a Knuth shuffle, but when the question was posed to me, I'd never heard of it and came up with a... slightly different method. 

I knew hashfunctions should do a good job of randomly distributing numbers, and for a good hash function, a single bit flip in the input should on average flip half the output bits (the avalanche property).  I knew this, because I've worked with optimizing hashtables, and they rely on this. 

So, to randomize an input you can concatenate the inputs with a random seed value, and then insert into a hashtable (as long as collision resolution is smart enough to distribute collisions above and below the number equally). 

This is the proof that as weird as it sounds, this method actually works and provides a good amount of randomness.  It is also very amenable to parallel execution (the bulk of computation is in the hashing, and a good concurrent hashtable implementation should experience near-zero lock contention on parallel insertion). 



