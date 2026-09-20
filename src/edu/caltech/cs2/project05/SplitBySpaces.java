package edu.caltech.cs2.project05;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SplitBySpaces {

    private static final int[] dictIndices = {10, 25, 35, 40, 50, 55, 60, 70, 80, 95};
    private static final double mult = 1;
    private static SortedSet<String> dictionary = new TreeSet<>();
    private static Map<String, Double> likelihoods = new HashMap<>();

    /**
     * Loads a dictionary of words, storing them in dictionary
     */
    public static void loadDictionary() throws FileNotFoundException {
        File file = new File(System.getProperty("user.dir") + "/tests/data/dictionary50.txt");
        Scanner scan = new Scanner(file);
        String wordToAdd;
        while (scan.hasNext()) {
            wordToAdd = scan.next().toLowerCase();
            if (wordToAdd.length() > 2) {
                dictionary.add(wordToAdd);
            }
        }
        scan.close();
        file = new File(System.getProperty("user.dir") + "/tests/data/scrabble.txt");
        scan = new Scanner(file);
        while (scan.hasNext()) {
            wordToAdd = scan.next().toLowerCase();
            if (wordToAdd.length() == 2) {
                dictionary.add(wordToAdd);
            }
        }
        scan.close();
        dictionary.add("a");
        dictionary.add("i");
    }

    /**
     * Loads a dictionary of quadgrams, saving their likelihoods in likelihoods
     */
    public static void loadOneGrams() throws FileNotFoundException {
        for (int i = 0; i < dictIndices.length; i++) {
            try (Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + "/tests/data/dictionary" + dictIndices[i] + ".txt"))) {
                String scanOutput = scanner.next();
                while (!scanOutput.equals("---")) {
                    scanOutput = scanner.next();
                }
                while (scanner.hasNext()) {
                    String word = scanner.next();
                    if (!likelihoods.containsKey(word)) {
                        double weight = dictIndices.length - i + word.length();
                        if (word.charAt(word.length() - 1) == 's') {
                            weight += 0.5;
                        }
                        if (Character.isUpperCase(word.charAt(0))) {
                            weight += 0.5;
                        }
                        likelihoods.put(word.toLowerCase(), weight);
                    }
                }
            }
        }
    }

    /**
     * Given a string s containing only alphabetic characters and spaces,
     * returns the best placement of spaces to create the most sensical
     * sentence of English words
     * @param s The string to split
     * @return the split string.
     */
    public static String splitBySpaces(String s) {
        return splitBySpacesHelper(s.replace(" ", "").toLowerCase(), new HashMap<>());
    }

    private static String splitBySpacesHelper(String s, HashMap<String, String> memo) {
        //Try all prefixes possible, and then recursively split the rest of the string.
        if (s.isEmpty()) {
            return s;
        } else if (memo.containsKey(s)) {
            return memo.get(s);
        } else {
            String bestSplit = s;
            double bestScore = 0.0;
            if (dictionary.contains(s)) {
                bestScore = getScore(s);
            }

            for (int i = 1; i < s.length(); i++) {
                String prefix = s.substring(0, i);
                if (dictionary.contains(prefix)) {
                    String attempt = prefix + " " + splitBySpacesHelper(s.substring(i), memo);
                    double attemptScore = getScore(attempt);

                    if (attemptScore > bestScore) {
                        bestSplit = attempt;
                        bestScore = attemptScore;
                    }
                }
            }

            memo.put(s, bestSplit);
            return bestSplit;
        }
    }

    /**
     * Given a word, returns its score as a sum of the quadgrams that make up the word.
     * @param query, the string to be tested
     * @returns the string's score
     */
    public static double getScore(String query) {
        double score = 0.0;
        String[] words = query.split(" ");
        for (String word : words) {
            if (likelihoods.containsKey(word)) {
                score += likelihoods.get(word);
            } else {
                return 0.0;
            }
        }
        return score / words.length;
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        loadDictionary();
        loadOneGrams();
        System.out.println(splitBySpaces("b e a v e r s s e a r c h f o r b r a n c h e s a n d t h e y u s e b i n a r y s e a r c h"));
    }
}
