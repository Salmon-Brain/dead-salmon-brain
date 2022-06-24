package ai.salmonbrain.client.hash;

public interface HashFunction<T> {
    long getHash(T key, String salt);
}
