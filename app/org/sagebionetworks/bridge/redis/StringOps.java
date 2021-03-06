package org.sagebionetworks.bridge.redis;

/**
 * For Redis strings (simple key-value pairs).
 */
public interface StringOps {
    
    /**
     * The specified key will expire after seconds.
     * 
     * @param key
     *            target key.
     * @param seconds
     *            number of seconds until expiration.
     * @return success code
     *          1 if successful, 0 if key doesn't exist or timeout could not be set
     */
    RedisOp<Long> expire(String key, int seconds);
    
    /**
     * Sets the value of the key and makes it expire after the specified
     * seconds.
     * 
     * @param key
     *            key of the key-value pair.
     * @param seconds
     *            number of seconds until expiration.
     * @param value
     *            value of the key-value pair.
     */
    RedisOp<String> setex(String key, int seconds, String value);

    /**
     * Sets the value of the key if and only if the key does not already have a
     * value.
     * 
     * @param key
     *            key of the key-value pair.
     * @param value
     *            value of the key-value pair.
     * @return success code
     *          1 if the key was set, 0 if not
     */
    RedisOp<Long> setnx(String key, String value);

    /**
     * Gets the value of the specified key. If the key does not exist null is
     * returned.
     */
    RedisOp<String> get(String key);

    /**
     * Deletes the value of the specified key.
     * 
     * @param key
     *            key of the key-value pair
     * @return numKeysDeleted
     *          the number of keys deleted
     */
    RedisOp<Long> delete(String key);

    /**
     * Determines the time until expiration for a key (time-to-live).
     * 
     * @param key
     *            key of the key-value pair.
     * @return ttl 
     *      positive value if ttl is set, zero if not, negative if there was an error
     */
    RedisOp<Long> ttl(String key);

    /**
     * Decrement the value by one
     * @param key
     * @return the new value of the key (after incrementing).
     */
    
    RedisOp<Long> increment(String key);
    
    /**
     * Decrement the value by one.
     * @param key
     * @return the new value of the key (after decrementing).
     */
    RedisOp<Long> decrement(String key);

}
