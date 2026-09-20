package edu.caltech.cs2.project05;

import edu.caltech.cs2.helpers.*;
import edu.caltech.cs2.interfaces.IStyleTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
@Tag("C")
public class PageRankTests {
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

    @DisplayName("graphFromJSON Functionality")
    @Nested
    class GraphFromJSONFunctionality {
        @Order(0)
        @DisplayName("graphFromJSON works correctly")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testGraphFromJSONBasic(int caseNum) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, NoSuchFieldException {
            String caseDataStr = null;
            FileInputStream fis = null;
            try {
                caseDataStr = Files.readString(Paths.get("./tests/data/" + caseNum + ".txt"), StandardCharsets.UTF_8);
                fis = new FileInputStream("./tests/data/" + caseNum + "_graphFromJSON.ser");
            }
            catch (IOException e) {
                System.out.println("Test files not found. Please contact a member of course staff.");
            }
            JSON database = JSON.parse(caseDataStr);
            SearchEngine sE = new SearchEngine((JSONObject) database);
            Class c = Class.forName("edu.caltech.cs2.project05.SearchEngine");
            Method method = c.getDeclaredMethod("graphFromJSON");
            method.setAccessible(true);
            Object actualGraph = method.invoke(sE, null);
            Field f = actualGraph.getClass().getDeclaredField("adjacencyList");
            f.setAccessible(true);
            Object actual = f.get(actualGraph);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object expected = ois.readObject();
            assertEquals(expected, actual);
        }
    }


    @DisplayName("PageRank Basic Functionality")
    @Nested
    class BasicFunctionality {
        private static List<HashMap<String, Double>> outputsBasic = List.of(
                new HashMap<>(),
                new HashMap<>(Map.of("A", 1.0)),
                new HashMap<>(Map.of("A", 0.3508771929824562, "B", 0.6491228070175439)),
                new HashMap<>(Map.of("A", 0.21991381963681134, "B", 0.42920898738073254, "C", 0.13096337334564484, "D", 0.21991381963681134)),
                new HashMap<>(Map.of("A", 0.16092343012209237, "B", 0.31981980642248176, "C", 0.16092343012209237, "D", 0.025000000000000005, "E", 0.16666666666666666, "F", 0.16666666666666666)),
                new HashMap<>(Map.of("A", 0.051704745757021275, "B", 0.07367926270375533, "C", 0.05741241249643272, "D", 0.34870368521481643, "E", 0.19990381197331825, "F", 0.2685960818546559))
        );

        @Order(0)
        @DisplayName("page rank works correctly in basic cases")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testPageRankBasic(int caseNum) {
            String caseDataStr = null;
            try {
                caseDataStr = Files.readString(Paths.get("./tests/data/" + caseNum + ".txt"), StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                System.out.println("Test files not found. Please contact a member of course staff.");
            }
            JSON database = JSON.parse(caseDataStr);
            SearchEngine sE = new SearchEngine((JSONObject) database);
            HashMap<String, Double> expected = outputsBasic.get(caseNum);
            HashMap<String, Double> actual = sE.pageRank();
            double rankSum = 0.0;
            assertEquals(expected.size(), actual.size());
            for (String k: actual.keySet()) {
                double d = actual.get(k);
                double e = expected.get(k);
                assertTrue(e - MAX_ERROR < d && e + MAX_ERROR > d, "Ranks do not match expectation");
            }
        }
    }
}

/*
Latin text in tests taken from https://www.thelatinlibrary.com/caesar/gall1.shtml (De Bello Gallico (Julius Caesar)) or Vergil's Aeneid
English text translations of De Bello Gallico in tests taken from https://www.perseus.tufts.edu/hopper/
Lyrics to 'The Duck Song' in tests taken from https://youtu.be/MtN1YnoL46Q
All text from 7.txt taken from Wikipedia.
Trees poem taken from Joyce Kilmer, 1913
*/