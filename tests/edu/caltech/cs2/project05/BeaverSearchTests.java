package edu.caltech.cs2.project05;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.IStyleTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
@Tag("C")
public class BeaverSearchTests {
    private static final String MANAGER_SOURCE = "src/edu/caltech/cs2/project05/SearchEngine.java";
    private static final double MAX_ERROR = 1E-4;

    @DisplayName("Style")
    @Nested
    class StyleTests implements IStyleTests {
        @Override
        public String getSource() {
            return MANAGER_SOURCE;
        }

        @Override
        public Class<?> getClazz() {
            return SearchEngine.class;
        }

        @Override
        public List<String> getPublicInterface() {
            return List.of(
                    "getLevenshtein", "search", "pageRank");
        }

        @Override
        public int getMaxFields() {
            return 4;
        }

        @Override
        public List<String> methodsToBanSelf() {
            return List.of();
        }

        @Order(0)
        @DisplayName("Does not use or import disallowed classes")
        @TestHint("Remember that you're not allowed to use anything in java.util except List and ArrayList! Do not import java.util.*!")
        @Test
        @Override
        public void testForInvalidClasses() {
            List<String> regexps = List.of(
                    "java\\.lang\\.reflect",
                    "java\\.io\\.(?!File|FileNotFoundException)");
            Inspection.assertNoImportsOf(getSource(), regexps);
            Inspection.assertNoUsageOf(getSource(), regexps);
        }
    }

    @DisplayName("getWordBagDict Functionality")
    @Nested
    class WordBagDictFunctionality {
        @Order(0)
        @DisplayName("getWordBagDict works correctly")
        @DependsOn("getWordBagDict")
        @TestHint("Don't forget to set the field wordBagDict!")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testWordBagBasic(int caseNum) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException, IOException {
            String caseDataStr = null;
            FileInputStream fis = null;
            try {
                caseDataStr = Files.readString(Paths.get("./tests/data/" + caseNum + ".txt"), StandardCharsets.UTF_8);
                fis = new FileInputStream("./tests/data/" + caseNum + "_wordBagDict.ser");
            }
            catch (IOException e) {
                System.out.println("Test files not found. Please contact a member of course staff.");
            }
            Class c = Class.forName("edu.caltech.cs2.project05.SearchEngine");
            JSON database = JSON.parse(caseDataStr);
            SearchEngine sE = new SearchEngine((JSONObject) database);
            Field wBD = c.getDeclaredField("wordBagDict");
            wBD.setAccessible(true);
            Object actual = wBD.get(sE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object expected = ois.readObject();
            assertEquals(expected, actual);
        }
    }

    @DisplayName("getRelevantLinks Functionality")
    @Nested
    class RelevantLinksFunctionality {
        private List<Map<String, Integer>> outputsBasic = List.of(
                Map.of(),
                Map.of("A", 1),
                Map.of("A", 1),
                Map.of("A", 1, "B", 1,"D",1),
                Map.of("A", 1, "B", 1, "E", 1),
                Map.of("C", 1, "F", 1)
        );
        @Order(0)
        @DisplayName("getRelevantLinks works correctly")
        @DependsOn("getWordBagDict, getRelevantLinks")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testRelevantLinksBasic(int caseNum) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            String caseDataStr = null;
            try {
                caseDataStr = Files.readString(Paths.get("./tests/data/" + caseNum + ".txt"), StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                System.out.println("Test files not found. Please contact a member of course staff.");
            }
            Class c = Class.forName("edu.caltech.cs2.project05.SearchEngine");
            JSON database = JSON.parse(caseDataStr);
            Object obj = new SearchEngine((JSONObject) database);
            Method method = c.getDeclaredMethod("getRelevantLinks", String.class);
            method.setAccessible(true);
            Object actual = method.invoke(obj, "searchquery");
            Map<String, Integer> expected = outputsBasic.get(caseNum);
            assertEquals(expected, actual);
        }
    }

    @DisplayName("Basic Functionality")
    @Nested
    class BasicFunctionality {
        private static List<ArrayList<String>> outputsBasic = List.of(
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("A")),
                new ArrayList<>(Arrays.asList("A")),
                new ArrayList<>(Arrays.asList("A", "B", "D")),
                new ArrayList<>(Arrays.asList("A", "E", "B")),
                new ArrayList<>(Arrays.asList("C", "F"))
        );

        @Order(0)
        @DisplayName("search works correctly")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testSearchEngineBasic(int caseNum) {
            String caseDataStr = null;
            try {
                caseDataStr = Files.readString(Paths.get("./tests/data/" + caseNum + ".txt"), StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                System.out.println("Test files not found. Please contact a member of course staff.");
            }
            JSON database = JSON.parse(caseDataStr);
            SearchEngine sE = new SearchEngine((JSONObject) database);
            ArrayList<String> expected = outputsBasic.get(caseNum);
            ArrayList<String> actual = sE.search("searchquery", 10);
            assertEquals(expected, actual);
        }
    }

    /*
    @Order(0)
    @DisplayName("pageRank works correctly for large wikipedia input")
    @ParameterizedTest(name = "Case {0} works correctly")
    @ValueSource(ints = {0})
    public void testPageRankWikipedia(int caseNum) throws IOException, ClassNotFoundException {
        String caseDataStr = null;
        try {
            caseDataStr = Files.readString(Paths.get("./tests/data/wikipedia.txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("Test files not found. Please contact a member of course staff.");
        }
        JSON database = JSON.parse(caseDataStr);
        SearchEngine sE = new SearchEngine((JSONObject) database);
        HashMap<String, Double> actual = sE.pageRank();
        double rankSum = 0.0;
        for (Double d : actual.values()) {
            rankSum += d;
        }
        assertTrue(1.0 - MAX_ERROR < rankSum && 1.0 + MAX_ERROR > rankSum, "Ranks do not sum up to 1.0.");
        FileInputStream fis = new FileInputStream("./tests/data/wikipediaOutput_pageRank.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object expected = ois.readObject();
        assertEquals(expected, actual);
//        FileWriter fw = new FileWriter("wikipediaPageRank.txt");
//        fw.write(actual.toString());
//        fw.close();
//        FileOutputStream fos = new FileOutputStream("wikipediaOutput_pageRank.ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(actual);
//        oos.close();
//        ArrayList<String> actualSearch = sE.search("chicken", 10);
//        for (String s : actualSearch) {
//            System.out.println(s);
//        }
    }
     */

    @DisplayName("Levenshtein Functionality")
    @Nested
    @Tag("B")
    class LevenshteinFunctionality {
        private List<String> inputs1 = List.of(
                "Among us",
                "Chicken",
                "short",
                "yortledoodle",
                "alligator"
        );

        private List<String> inputs2 = List.of(
                "Analagous",
                "Jockey",
                "horse",
                "yortyortyort",
                "rabbit"
        );

        private List<Integer> outputs = List.of(
                5,
                4,
                3,
                8,
                7
        );

        @Order(0)
        @DisplayName("Levenshtein algorithm has been correctly implemented")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4})
        public void testLevenshtein(int caseNum) throws IOException, ClassNotFoundException {
            int expected = outputs.get(caseNum);
            int actual = SearchEngine.getLevenshtein(inputs1.get(caseNum), inputs2.get(caseNum));
            assertEquals(expected, actual);
        }
    }

    /*
    @DisplayName("Advanced Functionality")
    @Nested
    class AdvancedFunctionality {
        private static List<ArrayList<String>> outputs = List.of(
                new ArrayList<>(Arrays.asList("Chicken",
                        "List_of_chicken_dishes",
                        "Chicken_as_food",
                        "Tandoori_chicken",
                        "Fried_chicken",
                        "Butter_chicken",
                        "Chicken_nuggets",
                        "Chicken_soup",
                        "Hainanese_chicken_rice",
                        "Meat"
                )),
                new ArrayList<>(Arrays.asList("Hawaii",
                        "Honolulu",
                        "Hawaiian_Islands",
                        "USA",
                        "Barack_Obama",
                        "United_States",
                        "United_States_of_America",
                        "Volcano",
                        "Water",
                        "Native_Hawaiians")),
                new ArrayList<>(Arrays.asList("Joe_Biden",
                        "Bernie_Sanders",
                        "Donald_Trump",
                        "Barack_Obama",
                        "Kristi_Noem",
                        "Jimmy_Carter",
                        "Jerome_Powell",
                        "Vice_President_of_the_United_States",
                        "United_States_Attorney_General",
                        "United_States_Secretary_of_Defense")),
                new ArrayList<>(Arrays.asList("Caltech",
                        "Linus_Pauling",
                        "Eris_(dwarf_planet)",
                        "Water",
                        "Water_(molecule)",
                        "Haumea",
                        "Jupiter",
                        "Bow_shock",
                        "Solar_System",
                        "Albert_Einstein")),
                new ArrayList<>(Arrays.asList("Atom",
                        "Atomic_structure",
                        "Atomic_nucleus",
                        "Chemistry",
                        "Atomic_mass",
                        "Atomic_number",
                        "Atomic_theory",
                        "Atomic_weight",
                        "Electron",
                        "Water"))
        );

        private static List<String> inputs = List.of(
                "chicken",
                "hawaii",
                "Joe Biden",
                "caltech",
                "atom"
        );

        @Order(0)
        @DisplayName("Levenshtein Algorithm has been correctly implemented")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4})
        public void testWikipedia(int caseNum) throws IOException, ClassNotFoundException {
            String caseDataStr = null;
            try {
                caseDataStr = Files.readString(Paths.get("./tests/data/wikipedia.txt"), StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                System.out.println("Test files not found. Please contact a member of course staff.");
            }
            JSON database = JSON.parse(caseDataStr);
            SearchEngine sE = new SearchEngine((JSONObject) database);
            String wikiStart = "https://simple.wikipedia.org/wiki/";
            ArrayList<String> expected = outputs.get(caseNum);
            for (int i = 0; i < expected.size(); i++) {
                expected.set(i, wikiStart + expected.get(i));
            }
            int total = 15;
            int max = 10;
            ArrayList<String> actual = sE.search(inputs.get(caseNum), total);
            for (int i = max; i < total; i ++) {
                actual.remove(max);
            }
            assertEquals(expected, actual);
        }
    }
        */

}

/*
Latin text in tests taken from https://www.thelatinlibrary.com/caesar/gall1.shtml (De Bello Gallico (Julius Caesar)) or Vergil's Aeneid
English text translations of De Bello Gallico in tests taken from https://www.perseus.tufts.edu/hopper/
Lyrics to 'The Duck Song' in tests taken from https://youtu.be/MtN1YnoL46Q
Text for split tests are adapted from various pages on wikipedia.org
*/