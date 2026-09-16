package com.dsa.similarity.algorithms;

import java.util.*;

/**
 * Aho-Corasick Multi-Pattern String Matching Automaton (CO2).
 * Constructs a trie with failure transitions (similar to KMP on a tree)
 * to locate multiple keyword/code signatures in a single linear scan O(N + matches).
 */
public class AhoCorasick {

    public static class Node {
        public final Map<Character, Node> children = new HashMap<>();
        public Node failLink = null;
        public final List<String> matchedPatterns = new ArrayList<>();
    }

    private final Node root = new Node();

    public void insertPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) return;
        Node curr = root;
        for (char c : pattern.toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new Node());
        }
        curr.matchedPatterns.add(pattern);
    }

    public void buildFailureLinks() {
        Queue<Node> queue = new LinkedList<>();

        for (Node child : root.children.values()) {
            child.failLink = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node curr = queue.poll();

            for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                Node fail = curr.failLink;
                while (fail != null && !fail.children.containsKey(ch)) {
                    fail = fail.failLink;
                }
                child.failLink = (fail != null) ? fail.children.get(ch) : root;
                child.matchedPatterns.addAll(child.failLink.matchedPatterns);
                queue.add(child);
            }
        }
    }

    public Map<String, List<Integer>> search(String text) {
        Map<String, List<Integer>> results = new HashMap<>();
        if (text == null || text.isEmpty()) return results;

        Node curr = root;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            while (curr != root && !curr.children.containsKey(ch)) {
                curr = curr.failLink;
            }
            if (curr.children.containsKey(ch)) {
                curr = curr.children.get(ch);
            }

            for (String pattern : curr.matchedPatterns) {
                int startPos = i - pattern.length() + 1;
                results.computeIfAbsent(pattern, k -> new ArrayList<>()).add(startPos);
            }
        }
        return results;
    }
}
