package chrisellepola;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SortUtilTest {

    private List<TopLevelValueObjectForTest> list_UnderTest_NoTies;
    private List<TopLevelValueObjectForTest> list_Immutable_NoTies;
    private final int[] noTiesSameOrder = new int[]{ 0, 1, 2, 3, 4 };
    private final int[] noTiesReverseOrder = new int[]{ 4, 3, 2, 1, 0 };

    private List<TopLevelValueObjectForTest> list_UnderTest_WithTies;
    private List<TopLevelValueObjectForTest> list_Immutable_WithTies;
    private final int[] withTiesSameOrder = new int[]{ 0, 1, 2, 3, 4, 5, 6 };
    private final int[] withTiesReverseOrder = new int[]{ 6, 5, 4, 3, 2, 1, 0 };

    public class TopLevelValueObjectForTest {

        public final String stringVar;
        public final int intVar;
        public final Integer integerBoxedVar;
        public final char charVar;
        public final float floatVar;
        public final boolean booleanVar;

        public TopLevelValueObjectForTest(
                String stringVar, int intVar, Integer integerBoxedVar, char charVar, float floatVar,
                boolean booleanVar){

            this.stringVar = stringVar;
            this.intVar = intVar;
            this.integerBoxedVar = integerBoxedVar;
            this.charVar = charVar;
            this.floatVar = floatVar;
            this.booleanVar = booleanVar;
        }
        // no getters on purpose:  to force Java Reflection
    }

    @Before
    public void setup(){
        list_Immutable_NoTies =
            Arrays.asList(
                new TopLevelValueObjectForTest("aA", 0, 0, 'a', 0.0f, false),
                new TopLevelValueObjectForTest("Bb", 1, -1, 'b', 0.1f, false),
                new TopLevelValueObjectForTest("cC", 2, -2, 'C', 0.2f, true),
                new TopLevelValueObjectForTest("Dd", 3, -3, 'd', 0.3f, true),
                new TopLevelValueObjectForTest("eE", 4, -4, 'E', 0.4f, true));
        list_UnderTest_NoTies = new ArrayList<>(list_Immutable_NoTies);

        list_Immutable_WithTies =
            Arrays.asList(
                new TopLevelValueObjectForTest("aA", 0, 0, 'a', -0.6f, false),
                new TopLevelValueObjectForTest("Bb", 1, -1, 'B', -0.5f, false),
                new TopLevelValueObjectForTest("bB", 1, -1, 'b', -0.5f, true),
                new TopLevelValueObjectForTest("bB", 3, -3, 'D', -0.2f, true),
                new TopLevelValueObjectForTest("Bb", 4, -4, 'e', -0.1f, true),
                new TopLevelValueObjectForTest("bB", 4, -4, 'E', -0.1f, false),
                new TopLevelValueObjectForTest("fF", 6, -6, 'g', 0f, true));
        list_UnderTest_WithTies = new ArrayList<>(list_Immutable_WithTies);
    }

    /*
     * TESTS
     */

    @Test
    public void testSortAnyObjectList_StringAscending(){
        testSort_ScrambledAndNot(
            new String[]{ "stringVar", "intVar", "integerBoxedVar", "charVar", "floatVar", "booleanVar" },
            noTiesSameOrder, new int[] { 0, 1, 2, 3, 5, 4, 6 });
    }
    @Test
    public void testSortAnyObjectList_StringDescending(){
        testSort_ScrambledAndNot(
            new String[]{ "-stringVar", "intVar", "integerBoxedVar", "charVar", "floatVar", "booleanVar" },
            noTiesReverseOrder, new int[] { 6, 1, 2, 3, 5, 4, 0 });
    }

    @Test
    public void testsortTopLevelValueObjectList_primitiveAscending(){
        testSort_ScrambledAndNot(
            new String[]{ "intVar", "stringVar", "integerBoxedVar", "charVar", "floatVar", "booleanVar" },
            noTiesSameOrder, new int[] { 0, 1, 2, 3, 5, 4, 6 });
    }
    @Test
    public void testSortAnyObjectList_primitiveDescending(){
        testSort_ScrambledAndNot(
            new String[]{ "-intVar", "stringVar", "integerBoxedVar", "charVar", "floatVar", "booleanVar" },
            noTiesReverseOrder, new int[] { 6, 5, 4, 3, 1, 2, 0 });
    }

    @Test
    public void testSortAnyObjectList_BoxedPrimitiveAscending(){
        testSort_ScrambledAndNot(
            new String[]{ "integerBoxedVar", "stringVar", "intVar", "charVar", "floatVar", "booleanVar" },
            noTiesReverseOrder, new int[] { 6, 5, 4, 3, 1, 2, 0 });
    }
    @Test
    public void testSortAnyObjectList_BoxedPrimitive_Descending(){
        testSort_ScrambledAndNot(
            new String[]{ "-integerBoxedVar", "stringVar", "intVar", "charVar", "floatVar", "booleanVar" },
            noTiesSameOrder, new int[] { 0, 1, 2, 3, 5, 4, 6 });
    }

    @Test
    public void testSortAnyObjectList_char_Ascending(){
        testSort_ScrambledAndNot(
            new String[]{ "charVar", "integerBoxedVar", "stringVar", "intVar", "floatVar", "booleanVar" },
            noTiesSameOrder, new int[] { 0, 1, 2, 3, 5, 4, 6 });
    }
    @Test
    public void testSortAnyObjectList_char_Descending(){
        testSort_ScrambledAndNot(
            new String[]{ "-charVar", "integerBoxedVar", "stringVar", "intVar", "floatVar", "booleanVar" },
            noTiesReverseOrder, new int[]{ 6, 5, 4, 3, 1, 2, 0 });
    }

    @Test
    public void testSortAnyObjectList_float_Ascending(){
        testSort_ScrambledAndNot(
            new String[]{ "floatVar", "charVar", "integerBoxedVar", "stringVar", "intVar", "booleanVar" },
            noTiesSameOrder, new int[] { 0, 1, 2, 3, 5, 4, 6 });
    }
    @Test
    public void testSortAnyObjectList_float_Descending(){
        testSort_ScrambledAndNot(
            new String[]{ "-floatVar", "charVar", "integerBoxedVar", "stringVar", "intVar", "booleanVar" },
            noTiesReverseOrder, new int[]{ 6, 5, 4, 3, 1, 2, 0 });
    }

    @Test
    public void testSortAnyObjectList_OnePrecedence_Ascending() {
        testSort_ScrambledAndNot_DifferentWithTiesScrambleTestOrder(
            new String[]{ "integerBoxedVar" },
            noTiesReverseOrder, new int[]{ 6, 4, 5, 3, 1, 2, 0 }, new int[]{ 6, 5, 4, 3, 1, 2, 0 });
    }
    @Test
    public void testSortAnyObjectList_OnePrecedence_Descending(){
        testSort_ScrambledAndNot_DifferentWithTiesScrambleTestOrder(
            new String[]{ "-integerBoxedVar" },
            noTiesSameOrder, withTiesSameOrder, new int[]{ 0, 2, 1, 3, 4, 5, 6 });
    }

    /*
     * HELPERS
     */

    private void testSort_ScrambledAndNot(
            String[] sortPrecedences, int[] orderPer_UnderTest_NoTies, int[] orderPer_Immutable_WithTies){

        testSort_ScrambledAndNot(
            sortPrecedences,
            orderPer_UnderTest_NoTies, orderPer_Immutable_WithTies, orderPer_Immutable_WithTies);
    }
    private void testSort_ScrambledAndNot_DifferentWithTiesScrambleTestOrder(
            String[] sortPrecedences, int[] orderPer_UnderTest_NoTies, int[] orderPer_Immutable_WithTies,
            int[] orderPer_Immutable_WithTies_Scrambled){

        testSort_ScrambledAndNot(
            sortPrecedences,
            orderPer_UnderTest_NoTies, orderPer_Immutable_WithTies, orderPer_Immutable_WithTies_Scrambled);
    }
    private void testSort_ScrambledAndNot(
            String[] sortPrecedences, int[] orderPer_UnderTest_NoTies, int[] orderPer_Immutable_WithTies,
            int[] orderPer_Immutable_WithTies_Scrambled){
        // Test all except after scrambled on Ties
        testSort_ThenScramble_NoScrambleTest(sortPrecedences, orderPer_UnderTest_NoTies, orderPer_Immutable_WithTies);

        // now Test scrambled on Ties (its OK here)
        testSort_WithTies(sortPrecedences, orderPer_Immutable_WithTies_Scrambled);
    }

    private void testSort_ThenScramble_NoScrambleTest(
            String[] sortPrecedences, int[] orderPer_UnderTest_NoTies, int[] orderPer_Immutable_WithTies){
        // Assert on NoTies
        testSort_NoTies(sortPrecedences, orderPer_UnderTest_NoTies);
        // Test scrambled, on NoTies
        scrambleList(list_UnderTest_NoTies);
        testSort_NoTies(sortPrecedences, orderPer_UnderTest_NoTies);

        // Test on Ties
        testSort_WithTies(sortPrecedences, orderPer_Immutable_WithTies);
        // Test scrambled, on Ties
        scrambleList(list_UnderTest_WithTies);

        // DON'T Test scrambled on Ties (caller needs to do that after calling this method)
    }
    private void scrambleList(List<TopLevelValueObjectForTest> list_UnderTest){
        list_UnderTest.add(list_UnderTest.remove(0));
        list_UnderTest.add(0, list_UnderTest.remove(3));
        list_UnderTest.add(1, list_UnderTest.remove(2));
        // result with respect to pre-scrambled: {4, 2, 1, 3, 5, 6, 0}
    }
    private void testSort_NoTies(String[] sortPrecedences, int[] noTiesOrderExpected){
        testSort(sortPrecedences, list_UnderTest_NoTies, noTiesOrderExpected, list_Immutable_NoTies);
    }
    private void testSort_WithTies(String[] sortPrecedences, int[] orderPer_Immutable){
        testSort(sortPrecedences, list_UnderTest_WithTies, orderPer_Immutable, list_Immutable_WithTies);
    }
    private void testSort(
            String[] sortPrecedences, List<TopLevelValueObjectForTest> list_UnderTest,
            int[] orderPer_Immutable, List<TopLevelValueObjectForTest> list_Immutable){
        // Test
        SortUtil.sortTopLevelValueObjectList(list_UnderTest, Arrays.asList(sortPrecedences));
        // Assert
        for(int sortedListIdx = 0; sortedListIdx < list_Immutable.size(); ++sortedListIdx){
            assertEquals(list_Immutable.get(orderPer_Immutable[sortedListIdx]), list_UnderTest.get(sortedListIdx));
        }
    }

}
