package ai.salmonbrain.hash;

import ai.salmonbrain.client.hash.HashFunctions;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HashFunctionsTest {

    @Test
    public void testName() {
        int N = 10_000_000;
        Map<Long, Long> counts = new HashMap<>(N);
        int seed = 99999989;
        for (int i = 0; i < N; i++) {
//            counts.merge(HashFunctions.murmur3("" + i, seed), 1L, Long::sum);
            counts.merge(HashFunctions.murmur3("" +
//                    ThreadLocalRandom.current().nextLong()
                            UUID.randomUUID().toString()
                    , seed), 1L, Long::sum);
        }

        System.out.println(counts.size());
//        System.out.println(counts);

    }

    @Test
    public void test2() {
        System.out.println(HashFunctions.murmur3("TestID"
                , 99999989));
    }
}