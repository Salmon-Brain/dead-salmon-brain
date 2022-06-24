package ai.salmonbrain.client.hash;

public class LongMurmurHash implements HashFunction<Long> {
    @Override
    public long getHash(Long key, String salt) {
        return HashFunctions.murmur3(key, HashFunctions.toSeed(salt));
    }
}
