package edu.caltech.cs2.project05;

import edu.caltech.cs2.helpers.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestExtension.class)
@Tag("A")
public class SplitBySpaceTests {
    @DisplayName("Basic Functionality")
    @Nested
    class BasicFunctionality {
        private static List<String> inputsSplit = List.of(
                "thequickbrownfoxjumpsoverthelazydog",
                "threedimensionalcontinuumcontainingpositionsanddirections",
                "mythicalcreatureinNorthernWesternandCentralEuropeanfolklorethattraditionallyhastheshapeofagiantserpentmonster",
                "theintentionalactivitypeopleperformtosupporttheneedsanddesiresofthemselvesotherpeopleororganizations",
                "buffalobuffalobuffalobuffalobuffalobuffalobuffalobuffalo",
                "aprivateresearchuniversityinpasadenacaliforniaunitedstates"
        );

        private static List<String> outputsSplit = List.of(
                "the quick brown fox jumps over the lazy dog",
                "three dimensional continuum containing positions and directions",
                "mythical creature in northern western and central european folklore that traditionally has the shape of a giant serpent monster",
                "the intentional activity people perform to support the needs and desires of themselves other people or organizations",
                "buffalo buffalo buffalo buffalo buffalo buffalo buffalo buffalo",
                "a private research university in pasadena california united states"
        );

        @Order(0)
        @DisplayName("split by space works correctly")
        @ParameterizedTest(name = "Case {0} works correctly")
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testSearchEngineBasic(int caseNum) throws FileNotFoundException {
            SplitBySpaces.loadDictionary();
            SplitBySpaces.loadOneGrams();
            String expected = outputsSplit.get(caseNum);
            String actual = SplitBySpaces.splitBySpaces(inputsSplit.get(caseNum));
            assertEquals(expected, actual);
        }
    }
}
