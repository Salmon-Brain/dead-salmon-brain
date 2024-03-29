package ai.salmonbrain.client.hash;

public class HashFunctions {

    private static final int DEFAULT_STRING_SEED = 99999989;

    /**
     * The MurmurHash3 algorithm was created by Austin Appleby and placed in the private domain.
     * This java port was authored by Yonik Seeley and also placed into the private domain.
     * It has been modified by Konstantin Sobolev and, you guessed it, also placed in the private domain.
     * The author hereby disclaims copyright to this source code.
     * <p>
     * This produces exactly the same hash values as the final C++
     * version of MurmurHash3 and is thus suitable for producing the same hash values across
     * platforms.
     * <p>
     * The 32 bit x86 version of this hash should be the fastest variant for relatively short keys like ids.
     * murmurhash3_x64_128 is a good choice for longer strings or if you need more than 32 bits of hash.
     * <p>
     * Note - The x86 and x64 versions do _not_ produce the same results, as the
     * algorithms are optimized for their respective platforms.
     */
    private static final class MurmurHash3 {
        static final long c1 = 0x87c37b91114253d5L;
        static final long c2 = 0x4cf5ad432745937fL;

        /**
         * 128 bits of state
         */
        private static final class HashCode128 {
            /**
             * First part of the hash, use it if you only need 64-bit hash
             */
            private long val1;
            /**
             * Second part of the hash
             */
            private long val2;

            private HashCode128(long v1, long v2) {
                val1 = v1;
                val2 = v2;
            }

            private HashCode128() {
                this(0, 0);
            }

            private byte[] getBytes() {
                return new byte[]{(byte) val1, (byte) (val1 >>> 8), (byte) (val1 >>> 16), (byte) (val1 >>> 24), (byte) (val1 >>> 32), (byte) (val1 >>> 40), (byte) (val1 >>> 48), (byte) (val1 >>> 56), (byte) val2, (byte) (val2 >>> 8), (byte) (val2 >>> 16), (byte) (val2 >>> 24), (byte) (val2 >>> 32), (byte) (val2 >>> 40), (byte) (val2 >>> 48), (byte) (val2 >>> 56),};
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                final HashCode128 pair = (HashCode128) o;
                return val1 == pair.val1 && val2 == pair.val2;
            }

            @Override
            public int hashCode() {
                return (int) (val1 * 31 + val2);
            }

            public int toInt() {
                return (int) val1;
            }

            public long toLong() {
                return val1;
            }

            @Override
            public String toString() {
                byte[] bytes = getBytes();
                StringBuilder sb = new StringBuilder(2 * bytes.length);
                for (byte b : bytes) {
                    sb.append(hexDigits[(b >> 4) & 0xf]).append(hexDigits[b & 0xf]);
                }
                return sb.toString();
            }

            public static MurmurHash3.HashCode128 fromBytes(byte[] bytes) {
                return new HashCode128(getLongLittleEndian(bytes, 0), getLongLittleEndian(bytes, 8));
            }

            private static final char[] hexDigits = "0123456789abcdef".toCharArray();
        }

        private static int fmix32(int h) {
            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;
            return h;
        }

        private static long fmix64(long k) {
            k ^= k >>> 33;
            k *= 0xff51afd7ed558ccdL;
            k ^= k >>> 33;
            k *= 0xc4ceb9fe1a85ec53L;
            k ^= k >>> 33;
            return k;
        }

        /**
         * Gets a long from a byte buffer in little endian byte order.
         */
        private static long getLongLittleEndian(byte[] buf, int offset) {
            return ((long) buf[offset + 7] << 56)   // no mask needed
                    | ((buf[offset + 6] & 0xffL) << 48) | ((buf[offset + 5] & 0xffL) << 40) | ((buf[offset + 4] & 0xffL) << 32) | ((buf[offset + 3] & 0xffL) << 24) | ((buf[offset + 2] & 0xffL) << 16) | ((buf[offset + 1] & 0xffL) << 8) | ((buf[offset] & 0xffL));        // no shift needed
        }


        /**
         * Returns the MurmurHash3_x86_32 hash.
         */
        private static int murmurhash3_x86_32(byte[] data, int offset, int len, int seed) {

            final int c1 = 0xcc9e2d51;
            final int c2 = 0x1b873593;

            int h1 = seed;
            int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

            for (int i = offset; i < roundedEnd; i += 4) {
                // little endian load order
                int k1 = (data[i] & 0xff) | ((data[i + 1] & 0xff) << 8) | ((data[i + 2] & 0xff) << 16) | (data[i + 3] << 24);
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;

                h1 ^= k1;
                h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                h1 = h1 * 5 + 0xe6546b64;
            }

            // tail
            int k1 = 0;

            switch (len & 0x03) {
                case 3:
                    k1 = (data[roundedEnd + 2] & 0xff) << 16;
                    // fallthrough
                case 2:
                    k1 |= (data[roundedEnd + 1] & 0xff) << 8;
                    // fallthrough
                case 1:
                    k1 |= (data[roundedEnd] & 0xff);
                    k1 *= c1;
                    k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                    k1 *= c2;
                    h1 ^= k1;
            }

            // finalization
            h1 ^= len;

            // fmix(h1);
            h1 ^= h1 >>> 16;
            h1 *= 0x85ebca6b;
            h1 ^= h1 >>> 13;
            h1 *= 0xc2b2ae35;
            h1 ^= h1 >>> 16;

            return h1;
        }


        /**
         * Returns the MurmurHash3_x86_32 hash of the UTF-8 bytes of the String without actually encoding
         * the string to a temporary buffer.  This is more than 2x faster than hashing the result
         * of String.getBytes().
         */
        private static int murmurhash3_x86_32(CharSequence data, int offset, int len, int seed) {

            final int c1 = 0xcc9e2d51;
            final int c2 = 0x1b873593;

            int h1 = seed;

            int pos = offset;
            int end = offset + len;
            int k1 = 0;
            int k2 = 0;
            int shift = 0;
            int bits = 0;
            int nBytes = 0;   // length in UTF8 bytes


            while (pos < end) {
                int code = data.charAt(pos++);
                if (code < 0x80) {
                    k2 = code;
                    bits = 8;

                    /***
                     // optimized ascii implementation (currently slower!!! code size?)
                     if (shift == 24) {
                     k1 = k1 | (code << 24);
                     k1 *= c1;
                     k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                     k1 *= c2;
                     h1 ^= k1;
                     h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                     h1 = h1*5+0xe6546b64;
                     shift = 0;
                     nBytes += 4;
                     k1 = 0;
                     } else {
                     k1 |= code << shift;
                     shift += 8;
                     }
                     continue;
                     ***/

                } else if (code < 0x800) {
                    k2 = (0xC0 | (code >> 6)) | ((0x80 | (code & 0x3F)) << 8);
                    bits = 16;
                } else if (code < 0xD800 || code > 0xDFFF || pos >= end) {
                    // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
                    k2 = (0xE0 | (code >> 12)) | ((0x80 | ((code >> 6) & 0x3F)) << 8) | ((0x80 | (code & 0x3F)) << 16);
                    bits = 24;
                } else {
                    // surrogate pair
                    // int utf32 = pos < end ? (int) data.charAt(pos++) : 0;
                    int utf32 = (int) data.charAt(pos++);
                    utf32 = ((code - 0xD7C0) << 10) + (utf32 & 0x3FF);
                    k2 = (0xff & (0xF0 | (utf32 >> 18))) | ((0x80 | ((utf32 >> 12) & 0x3F))) << 8 | ((0x80 | ((utf32 >> 6) & 0x3F))) << 16 | (0x80 | (utf32 & 0x3F)) << 24;
                    bits = 32;
                }


                k1 |= k2 << shift;

                // int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
                // int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift

                shift += bits;
                if (shift >= 32) {
                    // mix after we have a complete word

                    k1 *= c1;
                    k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                    k1 *= c2;

                    h1 ^= k1;
                    h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
                    h1 = h1 * 5 + 0xe6546b64;

                    shift -= 32;
                    // unfortunately, java won't let you shift 32 bits off, so we need to check for 0
                    if (shift != 0) {
                        k1 = k2 >>> (bits - shift);   // bits used == bits - newshift
                    } else {
                        k1 = 0;
                    }
                    nBytes += 4;
                }

            } // inner

            // handle tail
            if (shift > 0) {
                nBytes += shift >> 3;
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
            }

            // finalization
            h1 ^= nBytes;

            // fmix(h1);
            h1 ^= h1 >>> 16;
            h1 *= 0x85ebca6b;
            h1 ^= h1 >>> 13;
            h1 *= 0xc2b2ae35;
            h1 ^= h1 >>> 16;

            return h1;
        }


        /**
         * Returns the MurmurHash3_x64_128 hash, placing the result in "out".
         */
        private static void murmurhash3_x64_128(byte[] key, int offset, int len, int seed, HashCode128 out) {
            // The original algorithm does have a 32 bit unsigned seed.
            // We have to mask to match the behavior of the unsigned types and prevent sign extension.
            long h1 = seed & 0x00000000FFFFFFFFL;
            long h2 = seed & 0x00000000FFFFFFFFL;


            int roundedEnd = offset + (len & 0xFFFFFFF0);  // round down to 16 byte block
            for (int i = offset; i < roundedEnd; i += 16) {
                long k1 = getLongLittleEndian(key, i);
                long k2 = getLongLittleEndian(key, i + 8);
                k1 *= c1;
                k1 = Long.rotateLeft(k1, 31);
                k1 *= c2;
                h1 ^= k1;
                h1 = Long.rotateLeft(h1, 27);
                h1 += h2;
                h1 = h1 * 5 + 0x52dce729;
                k2 *= c2;
                k2 = Long.rotateLeft(k2, 33);
                k2 *= c1;
                h2 ^= k2;
                h2 = Long.rotateLeft(h2, 31);
                h2 += h1;
                h2 = h2 * 5 + 0x38495ab5;
            }

            long k1 = 0;
            long k2 = 0;

            switch (len & 15) {
                case 15:
                    k2 = (key[roundedEnd + 14] & 0xffL) << 48;
                case 14:
                    k2 |= (key[roundedEnd + 13] & 0xffL) << 40;
                case 13:
                    k2 |= (key[roundedEnd + 12] & 0xffL) << 32;
                case 12:
                    k2 |= (key[roundedEnd + 11] & 0xffL) << 24;
                case 11:
                    k2 |= (key[roundedEnd + 10] & 0xffL) << 16;
                case 10:
                    k2 |= (key[roundedEnd + 9] & 0xffL) << 8;
                case 9:
                    k2 |= (key[roundedEnd + 8] & 0xffL);
                    k2 *= c2;
                    k2 = Long.rotateLeft(k2, 33);
                    k2 *= c1;
                    h2 ^= k2;
                case 8:
                    k1 = ((long) key[roundedEnd + 7]) << 56;
                case 7:
                    k1 |= (key[roundedEnd + 6] & 0xffL) << 48;
                case 6:
                    k1 |= (key[roundedEnd + 5] & 0xffL) << 40;
                case 5:
                    k1 |= (key[roundedEnd + 4] & 0xffL) << 32;
                case 4:
                    k1 |= (key[roundedEnd + 3] & 0xffL) << 24;
                case 3:
                    k1 |= (key[roundedEnd + 2] & 0xffL) << 16;
                case 2:
                    k1 |= (key[roundedEnd + 1] & 0xffL) << 8;
                case 1:
                    k1 |= (key[roundedEnd] & 0xffL);
                    k1 *= c1;
                    k1 = Long.rotateLeft(k1, 31);
                    k1 *= c2;
                    h1 ^= k1;
            }

            //----------
            // finalization

            h1 ^= len;
            h2 ^= len;

            h1 += h2;
            h2 += h1;

            h1 = fmix64(h1);
            h2 = fmix64(h2);

            h1 += h2;
            h2 += h1;

            out.val1 = h1;
            out.val2 = h2;
        }

        // String-optimized 128-bit version added by konstantin.sobolev@gmail.com

        /**
         * Returns the MurmurHash3_x86_128 hash of the UTF-8 bytes of the String without actually encoding
         * the string to a temporary buffer. Does not check if input is properly encoded.
         *
         * @param data   data to encode
         * @param offset start offset
         * @param len    length
         * @param seed   seed
         * @param buf19  temporary 19-byte buffer to use. New one will be allocated if {@code null}
         * @param out    output pair to write results to
         */
        private static void murmurhash3_x64_128(CharSequence data, int offset, int len, int seed, byte[] buf19, HashCode128 out) {
            final byte[] encoded = buf19 == null ? new byte[19] : buf19;

            // The original algorithm does have a 32 bit unsigned seed.
            // We have to mask to match the behavior of the unsigned types and prevent sign extension.
            long h1 = seed & 0x00000000FFFFFFFFL;
            long h2 = seed & 0x00000000FFFFFFFFL;

            int encOffset = 0;
            int bytes = 0;

            int pos = offset;
            int end = offset + len;

            while (true) {
                // decode at least 16 bytes
                while (encOffset < 16 && pos < end) {
                    char code = data.charAt(pos++);

                    if (code < 0x80) {
                        encoded[encOffset++] = (byte) code;
                    } else if (code < 0x800) {
                        encoded[encOffset++] = (byte) (0xc0 | code >> 6);
                        encoded[encOffset++] = (byte) (0x80 | (code & 0x3f));
                    } else if (code < 0xD800 || code > 0xDFFF || pos >= end) {
                        // we check for pos>=end to encode an unpaired surrogate as 3 bytes.
                        encoded[encOffset++] = (byte) (0xe0 | ((code >> 12)));
                        encoded[encOffset++] = (byte) (0x80 | ((code >> 6) & 0x3f));
                        encoded[encOffset++] = (byte) (0x80 | (code & 0x3f));
                    } else {
                        // surrogate pair
                        int utf32 = (int) data.charAt(pos++);
                        utf32 = ((code - 0xD7C0) << 10) + (utf32 & 0x3FF);
                        encoded[encOffset++] = (byte) (0xf0 | ((utf32 >> 18)));
                        encoded[encOffset++] = (byte) (0x80 | ((utf32 >> 12) & 0x3f));
                        encoded[encOffset++] = (byte) (0x80 | ((utf32 >> 6) & 0x3f));
                        encoded[encOffset++] = (byte) (0x80 | (utf32 & 0x3f));
                    }
                }

                if (encOffset > 15) {
                    long k1 = getLongLittleEndian(encoded, 0);
                    long k2 = getLongLittleEndian(encoded, 8);

                    k1 *= c1;
                    k1 = Long.rotateLeft(k1, 31);
                    k1 *= c2;
                    h1 ^= k1;
                    h1 = Long.rotateLeft(h1, 27);
                    h1 += h2;
                    h1 = h1 * 5 + 0x52dce729;
                    k2 *= c2;
                    k2 = Long.rotateLeft(k2, 33);
                    k2 *= c1;
                    h2 ^= k2;
                    h2 = Long.rotateLeft(h2, 31);
                    h2 += h1;
                    h2 = h2 * 5 + 0x38495ab5;

                    encoded[0] = encoded[16];
                    encoded[1] = encoded[17];
                    encoded[2] = encoded[18];
                    encOffset -= 16;
                    bytes += 16;
                } else {
                    bytes += encOffset;
                    break;
                }
            } // inner

            long k1 = 0;
            long k2 = 0;

            switch (encOffset & 15) {
                case 15:
                    k2 = (encoded[14] & 0xffL) << 48;
                case 14:
                    k2 |= (encoded[13] & 0xffL) << 40;
                case 13:
                    k2 |= (encoded[12] & 0xffL) << 32;
                case 12:
                    k2 |= (encoded[11] & 0xffL) << 24;
                case 11:
                    k2 |= (encoded[10] & 0xffL) << 16;
                case 10:
                    k2 |= (encoded[9] & 0xffL) << 8;
                case 9:
                    k2 |= (encoded[8] & 0xffL);
                    k2 *= c2;
                    k2 = Long.rotateLeft(k2, 33);
                    k2 *= c1;
                    h2 ^= k2;
                case 8:
                    k1 = ((long) encoded[7]) << 56;
                case 7:
                    k1 |= (encoded[6] & 0xffL) << 48;
                case 6:
                    k1 |= (encoded[5] & 0xffL) << 40;
                case 5:
                    k1 |= (encoded[4] & 0xffL) << 32;
                case 4:
                    k1 |= (encoded[3] & 0xffL) << 24;
                case 3:
                    k1 |= (encoded[2] & 0xffL) << 16;
                case 2:
                    k1 |= (encoded[1] & 0xffL) << 8;
                case 1:
                    k1 |= (encoded[0] & 0xffL);
                    k1 *= c1;
                    k1 = Long.rotateLeft(k1, 31);
                    k1 *= c2;
                    h1 ^= k1;
            }

            //----------
            // finalization

            h1 ^= bytes;
            h2 ^= bytes;

            h1 += h2;
            h2 += h1;

            h1 = fmix64(h1);
            h2 = fmix64(h2);

            h1 += h2;
            h2 += h1;

            out.val1 = h1;
            out.val2 = h2;
        }

        /**
         * Returns the MurmurHash3_x86_128 hash of the ASCII bytes of the String without actually encoding
         * the string to a temporary buffer. Warning: will return invalid results if {@code data}
         * contains non-ASCII characters! No checks are made.
         * Results are placed in {@code out}.
         */
        private static void murmurhash3_x64_128_ascii(CharSequence data, int offset, int len, int seed, HashCode128 out) {
            // The original algorithm does have a 32 bit unsigned seed.
            // We have to mask to match the behavior of the unsigned types and prevent sign extension.
            long h1 = seed & 0x00000000FFFFFFFFL;
            long h2 = seed & 0x00000000FFFFFFFFL;

            int pos = offset;
            int end = offset + len;

            while (pos <= end - 16) {
                long k1 = (data.charAt(pos++) & 0xffL);
                k1 |= (data.charAt(pos++) & 0xffL) << 8;
                k1 |= (data.charAt(pos++) & 0xffL) << 16;
                k1 |= (data.charAt(pos++) & 0xffL) << 24;
                k1 |= (data.charAt(pos++) & 0xffL) << 32;
                k1 |= (data.charAt(pos++) & 0xffL) << 40;
                k1 |= (data.charAt(pos++) & 0xffL) << 48;
                k1 |= (data.charAt(pos++) & 0xffL) << 56;

                long k2 = (data.charAt(pos++) & 0xffL);
                k2 |= (data.charAt(pos++) & 0xffL) << 8;
                k2 |= (data.charAt(pos++) & 0xffL) << 16;
                k2 |= (data.charAt(pos++) & 0xffL) << 24;
                k2 |= (data.charAt(pos++) & 0xffL) << 32;
                k2 |= (data.charAt(pos++) & 0xffL) << 40;
                k2 |= (data.charAt(pos++) & 0xffL) << 48;
                k2 |= (data.charAt(pos++) & 0xffL) << 56;

                k1 *= c1;
                k1 = Long.rotateLeft(k1, 31);
                k1 *= c2;
                h1 ^= k1;
                h1 = Long.rotateLeft(h1, 27);
                h1 += h2;
                h1 = h1 * 5 + 0x52dce729;
                k2 *= c2;
                k2 = Long.rotateLeft(k2, 33);
                k2 *= c1;
                h2 ^= k2;
                h2 = Long.rotateLeft(h2, 31);
                h2 += h1;
                h2 = h2 * 5 + 0x38495ab5;
            } // inner

            long k1 = 0;
            long k2 = 0;
            int tail = end;

            switch ((end - pos) & 15) {
                case 15:
                    k2 = (data.charAt(--tail) & 0xffL) << 48;
                case 14:
                    k2 |= (data.charAt(--tail) & 0xffL) << 40;
                case 13:
                    k2 |= (data.charAt(--tail) & 0xffL) << 32;
                case 12:
                    k2 |= (data.charAt(--tail) & 0xffL) << 24;
                case 11:
                    k2 |= (data.charAt(--tail) & 0xffL) << 16;
                case 10:
                    k2 |= (data.charAt(--tail) & 0xffL) << 8;
                case 9:
                    k2 |= (data.charAt(--tail) & 0xffL);
                    k2 *= c2;
                    k2 = Long.rotateLeft(k2, 33);
                    k2 *= c1;
                    h2 ^= k2;
                case 8:
                    k1 = (data.charAt(--tail) & 0xffL) << 56;
                case 7:
                    k1 |= (data.charAt(--tail) & 0xffL) << 48;
                case 6:
                    k1 |= (data.charAt(--tail) & 0xffL) << 40;
                case 5:
                    k1 |= (data.charAt(--tail) & 0xffL) << 32;
                case 4:
                    k1 |= (data.charAt(--tail) & 0xffL) << 24;
                case 3:
                    k1 |= (data.charAt(--tail) & 0xffL) << 16;
                case 2:
                    k1 |= (data.charAt(--tail) & 0xffL) << 8;
                case 1:
                    k1 |= (data.charAt(--tail) & 0xffL);

                    k1 *= c1;
                    k1 = Long.rotateLeft(k1, 31);
                    k1 *= c2;
                    h1 ^= k1;
            }

            //----------
            // finalization

            h1 ^= len;
            h2 ^= len;

            h1 += h2;
            h2 += h1;

            h1 = fmix64(h1);
            h2 = fmix64(h2);

            h1 += h2;
            h2 += h1;

            out.val1 = h1;
            out.val2 = h2;
        }
    }

    public static int toSeed(String salt) {
        return (int) murmur3(salt, DEFAULT_STRING_SEED);
    }

    public static long murmur3(long value, int seed) {
        byte[] data = new byte[8];
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (value & 0xFFL);
            value >>= 8;
        }
        return MurmurHash3.murmurhash3_x86_32(data, 0, 8, seed);
    }

    public static long murmur3(CharSequence charSequence, int seed) {
        return MurmurHash3.murmurhash3_x86_32(charSequence, 0, charSequence.length(), seed);
    }
}
