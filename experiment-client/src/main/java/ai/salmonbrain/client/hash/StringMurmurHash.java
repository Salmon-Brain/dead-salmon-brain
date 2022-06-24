package ai.salmonbrain.client.hash;

public class StringMurmurHash implements HashFunction<String> {
    @Override
    public long getHash(String key, String salt) {
        return HashFunctions.murmur3(key, HashFunctions.toSeed(salt));
    }
}
