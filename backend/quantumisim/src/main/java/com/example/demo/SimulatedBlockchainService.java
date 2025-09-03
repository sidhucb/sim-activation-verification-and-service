// In a new file: SimulatedBlockchainService.java
package com.example.demo;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimulatedBlockchainService {

    private final List<Block> blockchain = new ArrayList<>();
    private static final String GENESIS_BLOCK_HASH = "0";

    @PostConstruct
    private void initializeChain() {
        // Create the Genesis Block (the first block in the chain)
        Block genesisBlock = new Block("Genesis Block", "0", System.currentTimeMillis(), 0);
        blockchain.add(genesisBlock);
        System.out.println("--- Simulated Blockchain Initialized with Genesis Block ---");
    }

    // Simulates registering a new iSIM public key on the blockchain
    public void registerPublicKey(String publicKey) {
        String previousHash = blockchain.get(blockchain.size() - 1).calculateHash();
        Block newBlock = new Block(publicKey, previousHash, System.currentTimeMillis(), 0); // Nonce mining is omitted for simplicity
        blockchain.add(newBlock);
        System.out.println("BLOCKCHAIN: New public key registered on the chain. Height: " + blockchain.size());
    }

    // Simulates querying the blockchain to find a registered public key
    public Optional<String> findPublicKey(String publicKey) {
        System.out.println("BLOCKCHAIN: Querying for public key: " + publicKey.substring(0, 30) + "...");
        // Search the chain for the public key (skipping the genesis block)
        return blockchain.stream()
                .skip(1) // Skip Genesis Block
                .map(Block::data)
                .filter(key -> key.equals(publicKey))
                .findFirst();
    }
}