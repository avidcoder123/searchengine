package edu.caltech.cs2.project05;

import java.io.FileNotFoundException;
import java.util.*;

public class SearchEngine {
    private static final String SPACES_AND_PUNCT_PATTERN = "[\\s\\p{Punct}]+";
    private static final int MAX_ITERS = 100;
    private static final double DAMPING = .85;
    private static final double URL_FACTOR = 100.0;
    private JSONObject database;
    //    format is { link: {"content": pageText, "links": {other:number} }
    private Graph<String, Integer> wikiGraph;
    private HashMap<String, Double> pageRanks;
    private HashMap<String, HashMap<String, Integer>> wordBagDict;

    /**
     * Constructs the SearchEngine given the JSONObject database passed in
     * as a parameter.
     * @param database
     */
    public SearchEngine(JSONObject database) {
        this.database = database;
        this.wikiGraph = graphFromJSON();
        this.pageRanks = pageRank();
        this.wordBagDict = getWordBagDict();
    }

    /**
     * Given a String query, the search query, and an integer k,
     * returns an ArrayList of the first k links that match the query,
     * in order of their pagerank/relevancy.
     * @param query
     * @param k
     * @return ArrayList of first k links matching the query
     */
    public ArrayList<String> search(String query, int k) {
        ArrayList<String> results = new ArrayList<>();
        HashMap<String, Integer> linkRelevance = getRelevantLinks(query);
        HashMap<String, Double> relevantRanking = new HashMap<>();

        String[] queryWords = query.toLowerCase().split(SPACES_AND_PUNCT_PATTERN);
        for (String link : linkRelevance.keySet()) {
            boolean wordsInURL = true;
            for (String word : queryWords) {
                wordsInURL = wordsInURL && link.contains(word);
            }

            double score = linkRelevance.get(link);
            score *= pageRanks.get(link);
            if (wordsInURL) {
                score *= URL_FACTOR;
            }

            relevantRanking.put(link, score);
        }

        return getOrderedResults(query, linkRelevance, relevantRanking, k);
    }

    /**
     * Ranks pages according to the PageRank algorithm as outlined in the guide
     * @return a hashmap of links, whose values are the keys' pagerank values
     */
    public HashMap<String, Double> pageRank() {
        HashMap<String, Double> rankings = new HashMap<>();
        Set<String> sites = wikiGraph.vertices();
        for (String site : sites) {
            rankings.put(site, 1.0 / sites.size());
        }

        for (int i = 0; i < MAX_ITERS; i++) {
            HashMap<String, Double> newRankings = new HashMap<>();

            for (String site: sites) {
                //Redistribute a certain portion of the weights to all nodes
                newRankings.put(site, (1 - DAMPING) / sites.size());
            }

            for (String site : sites) {
                double distributeRank = rankings.get(site) * DAMPING;

                Set<String> neighbors = wikiGraph.neighbors(site);
                if (!neighbors.isEmpty()) {
                    //Distribute the rank among its neighbors
                    for (String neigh : neighbors) {
                        newRankings.put(neigh, newRankings.get(neigh) + distributeRank / neighbors.size());
                    }
                } else {
                    //For a "sink", distribute its rank among all pages.
                    for (String s : sites) {
                        newRankings.put(s, newRankings.get(s) + distributeRank / sites.size());
                    }
                }
            }

            rankings = newRankings;
        }

        return rankings;
    }

    /**
     * Returns a Graph representing the relations between links in the database.
     * For example, if on the webpage for link A, there are two links to link B, then
     * the edge (A, B) should have a weight of 2 in the graph.
     * @return a Graph of the database
     */
    private Graph<String, Integer> graphFromJSON() {
        Graph<String, Integer> relations = new Graph<>();

        for (String link : database.keySet()) {
            relations.addVertex(link);
            JSONObject outLinkMap = getNeighborsOfLink(link);

            for (String outLink : outLinkMap.keySet()) {
                relations.addVertex(outLink);
                relations.addEdge(link, outLink, ((JSONIntegerValue) outLinkMap.get(outLink)).get());
            }
        }

        return relations;
    }

    private int getMinMatches(String site, String[] words) {
        HashMap<String, Integer> wordBag = wordBagDict.get(site);
        int minMatches = Integer.MAX_VALUE;

        for (String word : words) {
            int matches = wordBag.getOrDefault(word, 0);
            if (matches == 0) {
                //Early exit
                return 0;
            } else {
                minMatches = Math.min(minMatches, matches);
            }
        }

        return minMatches;
    }

    /**
     * Given a String query, the search query, returns a HashMap whose keys are relevant links
     * whose webpages contain all the words in the query, and whose values are the minimum
     * number of times that any word in the query appears in the link's webpage.
     * For instance, if query is "dog toy", then this method returns a HashMap of all links
     * in the database whose webpage contents include "dog" and "toy", whose corresponding values
     * are the minimum number of times "dog" or "toy" appear on the link's webpage.
     * Capitalization does not matter.
     * @param query
     * @return a HashSet of links relevant to the query
     */
    private HashMap<String, Integer> getRelevantLinks(String query) {
        String[] queryWords = query.toLowerCase().split(" ");
        HashMap<String, Integer> matches = new HashMap<>();

        for (String site : wordBagDict.keySet()) {
            int minMatches = getMinMatches(site, queryWords);
            if (minMatches != 0) {
                matches.put(site, minMatches);
            }
        }

        return matches;
    }

    /**
     * Goes through the database and creates a "word bag" for each link.
     * Returns a HashMap mapping links (Strings) to their word bags (HashMaps mapping words to counts).
     * For instance, if the webpage of the link "https://www.caltechcs2.com" only has the contents,
     * "Computers do what computers do.", then the returned HashMap should have the entry
     * "https://www.caltechcs2.com": {"computers"=2, "do"=2, "what"=1}.
     * All words in the word bags should be all lower-case, and words should be split by punctuation.
     * @return a HashMap mapping links to the word bags
     */
    private HashMap<String, HashMap<String, Integer>> getWordBagDict() {
        HashMap<String, HashMap<String, Integer>> wordBag = new HashMap<>();

        for (String site : wikiGraph.vertices()) {
            String contents = getPageTextOfLink(site).get().toLowerCase();
            String[] words = contents.split(SPACES_AND_PUNCT_PATTERN);

            HashMap<String, Integer> wordCounts = new HashMap<>();
            for (String word : words) {
                int currentCount = wordCounts.getOrDefault(word, 0);
                wordCounts.put(word, currentCount + 1);
            }

            wordBag.put(site, wordCounts);
        }

        return wordBag;
    }


    /**
     * Returns the Levenshtein distance between two strings a and b
     */
    public static int getLevenshtein(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        if (a.isEmpty()) {
            return b.length();
        } else if (b.isEmpty()) {
            return a.length();
        } else {
            Integer[][] memo = new Integer[a.length() + 1][b.length() + 1];
            for (int i_a = a.length(); i_a >= 0; i_a--) {
                for (int i_b = b.length(); i_b >= 0; i_b--) {
                    if (i_a == a.length()) {
                        memo[i_a][i_b] = b.length() - i_b ;
                    } else if (i_b == b.length()) {
                        memo[i_a][i_b] = a.length() - i_a;
                    } else if (a.charAt(i_a) == b.charAt(i_b)) {
                        memo[i_a][i_b] = memo[i_a + 1][i_b + 1];
                    } else {
                        memo[i_a][i_b] = 1 + Math.min(
                                memo[i_a + 1][i_b],
                                Math.min(
                                        memo[i_a][i_b + 1],
                                        memo[i_a + 1][i_b + 1]
                                )
                        );
                    }
                }
            }

            return memo[0][0];
        }
    }

    /**
     * @param link the link to investigate
     * @return a JSONObject containing all the neighbors of a given link
     */
    private JSONObject getNeighborsOfLink(String link) {
        return (JSONObject) ((JSONObject) database.get(link)).get("links");
    }

    /**
     * @param link the link to investigate
     * @return the text on the webpage with the link link
     */
    private JSONValue<String> getPageTextOfLink(String link) {
        return (JSONValue<String>) ((JSONObject) database.get(link)).get("content");
    }

    /**
     * Takes in a query, a HashMap of words to the number of times they appear, relevantPageRank,
     * a HashMap of links and pagerank values, where the links
     * are a relevant subset of all the links in the database, and an integer maxResults.
     * Returns an ArrayList of up to the first maxResults links in relevantPageRank, sorted in the
     * order of highest pagerank value to lowest pagerank value.
     * @param query
     * @param counts
     * @param relevantPageRank
     * @param maxResults
     * @return an ArrayList of the first numResults links in relevantPageRank,
     * sorted in descending pagerank value order
     */
    private ArrayList<String> getOrderedResults(String query, HashMap<String, Integer> counts, HashMap<String, Double> relevantPageRank, int maxResults) {
        List<Map.Entry<String, Double>> orderedLinks =
                new ArrayList<>(relevantPageRank.entrySet());
        Collections.sort(orderedLinks, Collections.reverseOrder(Map.Entry.comparingByValue()));
        orderedLinks = new ArrayList<>(orderedLinks);
        HashMap<String, Double> linkDistance = new HashMap<>();
        int numResults = 0;
        for (Map.Entry<String, Double> entry : orderedLinks) {
            double mult = 1.0;
            String[] words = query.toLowerCase().split("\\s+");
            boolean linkContainsWords = true;
            String lowerLink = entry.getKey().toLowerCase();
            for (String word : words) {
                if (!lowerLink.contains(word)) {
                    linkContainsWords = false;
                    break;
                }
            }
            if (linkContainsWords) {
                mult = 0.5;
            }
            linkDistance.put(entry.getKey(), mult * getLevenshtein(entry.getKey().toLowerCase(), query.toLowerCase()) / counts.get(entry.getKey()));
            numResults++;
            if (numResults == maxResults) {
                break;
            }
        }
        orderedLinks = new ArrayList<>(linkDistance.entrySet());
        orderedLinks.sort(Map.Entry.comparingByValue());
        ArrayList<String> results = new ArrayList<>();
        for (Map.Entry<String, Double> entry : orderedLinks) {
            results.add(entry.getKey());
        }
        return results;
    }
}
