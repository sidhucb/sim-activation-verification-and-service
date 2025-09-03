// In a new file: Block.java
package com.example.demo;

import java.time.Instant;

public record Block(
    String data, // For simplicity, this will be the user's public key
    String previousHash,
    long timeStamp,
    int nonce
) {
    // In a real blockchain, you'd have a method to calculate the hash of this block
    public String calculateHash() {
        // A real implementation would use SHA-256 on all block properties
        // For simulation, we'll keep it simple.
        return String.valueOf(data.hashCode() + previousHash.hashCode() + timeStamp);
    }
}