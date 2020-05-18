package chrisellepola;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortUtil {

    /**
     * Sorts tList by sortFieldNamePrecedences (using Reflection):<br>
     * (1) Sort precedence is with respect to the order of sortFieldNamePrecedences<br>
     * (2) All sorts are ascending by default,
     *      except for sortFieldNamePrecedences that are prepended/flagged with a "-" dash (e.g: "-nameToSortDescending")<br>
     * (3) Sort fields must be top-level primitives, Boxed Primitives, or Strings in < T ><br>
     * (4) sortFieldNamePrecedences, ignoring possible prepended "-" dash (see 2),
     *      must be non-blank, valid, top-level, public variable names
     *      (matching spelling & character casing, without whitespace) in < T ><br>
     * <br>
     * In addition to sorting the tList, it is also printed & returned.<br>
     * <br>
     * Example Usage:<br>
     *     Given < T > Object-to-sort as...<br>
     *     <br>
     *     class SomeObject{<br>
     *         public String name0;<br>
     *         public int name1;<br>
     *         public Integer name2;<br>
     *     }<br>
     *     <br>
     *     ... with a `sortFieldNamePrecedences` param of<br>
     *     <br>
     *     {"-name2", "name0", "-name1"}<br>
     *     <br>
     *     ... then sorting will be made by (first) `name2` descending,
     *      (second) `name0` ascending, (third) `name1` descending.
     *
     * @param tList non-empty List< T >
     * @param sortFieldNamePrecedences
     *          Non-empty List< String > of [non-empty] names of variable fields in < T > to sort by
     * @param <T> type of Objects to be sorted
     *
     * @return sorted List< T >
     */
    static public <T> List<T> sortTopLevelValueObjectList(
            @NotEmpty final List<T> tList, @NotEmpty final List<String> sortFieldNamePrecedences){

        tList.sort(makeComparatorForPrecedence(sortFieldNamePrecedences));

        printSortedListWithHeaderFooter(tList, sortFieldNamePrecedences);
        return tList;
    }

    static public <T> Comparator<T> makeComparatorForPrecedence(@NotEmpty final List<String> sortFieldNamePrecedences){
        return new Comparator<T>() {
            @Override
            public int compare(T t0, T t1){
                // Compare based on sortFieldNamePrecedences values/order
                // until a non-0/non-equal comparison results, or else no sortFieldNamePrecedences remaining
                for(    int curSortPrecedence = 0;
                        curSortPrecedence < sortFieldNamePrecedences.size();
                        ++curSortPrecedence){
                    // Assess/Prep curSortFieldName
                    String curSortFieldName = sortFieldNamePrecedences.get(curSortPrecedence);
                    final boolean descending = curSortFieldName.startsWith("-");
                    if(descending){
                        // Trim off flag for descending
                        curSortFieldName = curSortFieldName.replace("-", "");
                    }

                    /*
                     * COMPARE */
                    final int comparison;
                    try{
                        comparison = compareByField(t0, t1, curSortFieldName, descending);

                    } catch(NoSuchFieldException | NullPointerException | SecurityException
                            | IllegalAccessException | IllegalArgumentException | ExceptionInInitializerError e){
                        throw new RuntimeException(
                            "Error getting field value (with index " + curSortPrecedence
                                + " in sortFieldNamePrecedences), for sorting.",
                            e);
                    }

                    // Assess if its necessary to continue comparing by subsequently-precedent sortField(s)
                    if(comparison != 0 || curSortPrecedence == sortFieldNamePrecedences.size() - 1){
                        // [Break] no more comparison necessary
                        return comparison;
                    }
                    // else continue (to compare with next-precedent sortField),
                    // as curSortFieldName comparison was equal (0)
                }

                return 0;   // reaching here means sortFieldNamePrecedences was a disallowed empty-List
            }
        };
    }

    @SuppressWarnings("unchecked")
    static public int compareByField(
                @NotNull Object object0, @NotNull Object object1, @NotEmpty String fieldName, boolean descending)
            throws NoSuchFieldException, IllegalAccessException{

        final Field curSortField = object0.getClass().getField(fieldName);
        final Object t0CurSortField = curSortField.get(object0);
        final Object t1CurSortField = curSortField.get(object1);
        final Class t0CurSortFieldClazz = t0CurSortField.getClass();
        if(t0CurSortFieldClazz.equals(String.class) || t0CurSortFieldClazz.equals(Character.class)){
            return compareStrings(t0CurSortField.toString(), t1CurSortField.toString(), descending);

        } else {
            return compareComparables(
                (Comparable<Object>) t0CurSortField, (Comparable<Object>) t1CurSortField, descending);
        }
    }

    static int compareStrings(String string0, String string1, boolean descending){
        if (descending) {
            return string1.compareToIgnoreCase(string0);

        } else {
            return string0.compareToIgnoreCase(string1);
        }
    }
    static int compareComparables(Comparable<Object> comparable0, Comparable<Object> comparable1, boolean descending){

        if (descending) {
            return comparable1.compareTo(comparable0);

        } else {
            return comparable0.compareTo(comparable1);
        }
    }

    /*
     * NOTE - the majority of code is below this line, but is JUST FOR PRINTING.
     * Refer to README for details about how it is not required for the main sorting algorithm itself.
     * Hopefully this clarifies how the size of the code below should not be interpreted as concluding
     * that the main algorithm itself is complicated, proportional to the number of below lines.
     */

    static public <T> void printSortedListWithHeaderFooter(
            @NotEmpty final List<T> tList, @NotEmpty final List<String> sortFieldNames){

        System.out.println(
            "Sorted List of " + tList.getClass().getCanonicalName()
            + " (only displaying values necessary for comparing input-adjacent Objects per order: "
            + sortFieldNames.toString() + "):");

        printSortedList(tList, sortFieldNames);

        // Format, to separate from any next print
        System.out.println();
    }
    /**
     * (in tList order) Print each T's field values that match the next T's values,
     *  according to the ordered sortFieldNames, up until (and including) the first non-matching value.
     *
     * @param tList
     * @param sortFieldNames
     * @param <T>
     */
    static private <T> void printSortedList(final List<T> tList, final List<String> sortFieldNames){
        final List<String> nextTValueStrings = new ArrayList<>();
        for(int curTListIdx = 0; curTListIdx < tList.size(); ++curTListIdx){
            /*
             * Print any [prior] nextTValueStrings (since they now correspond to curT) by CSV.
             * don't next-line yet, as more from this line may be printed yet */
            printCSVNoNewLine(nextTValueStrings);

            // Prep for the new curT/nextT
            final int sortFieldNamesIdxStartPrinting = nextTValueStrings.size(); // 1-after the last nextTValueStrings val
            nextTValueStrings.clear();

            /*
             * Print curT line, and load any nextT */
            printSortedListItemFieldsNotPrintedYetAndLoadNextT(
                curTListIdx, tList, sortFieldNamesIdxStartPrinting, sortFieldNames, nextTValueStrings);
            System.out.println();
        }
    }
    static void printCSVNoNewLine(final List<String> strings){
        for(int curStringIdx = 0; curStringIdx < strings.size(); ++curStringIdx){
            System.out.print(strings.get(curStringIdx));
            if(curStringIdx < strings.size() - 1){
                System.out.print(", ");
            }
        }
    }

    /**
     * Assess/Print values (starting at sortFieldNamesIdxStartPrinting) in curT by CSV,
     *  & Compare with nextT if exists (loading nextTValueStrings if so)
     *
     * @param tListItemIdx
     * @param tList
     * @param sortFieldNamesIdxStartPrinting
     * @param sortFieldNames
     * @param nextTValueStrings
     * @param <T>
     */
    static private <T> void printSortedListItemFieldsNotPrintedYetAndLoadNextT(
            int tListItemIdx, final List<T> tList,
            int sortFieldNamesIdxStartPrinting, final List<String> sortFieldNames,
            List<String> nextTValueStrings){

        for(int curSortFieldNamesIdx = 0; curSortFieldNamesIdx < sortFieldNames.size(); ++curSortFieldNamesIdx){
            String curSortFieldName = sortFieldNames.get(curSortFieldNamesIdx);
            // Remove any descending-order flag [irrelevant here]
            curSortFieldName = curSortFieldName.replace("-", "");

            if(tListItemIdx < tList.size() - 1){ // nextT exists
                // TODO:  does this block truly not matter?
                if(printItemField_LoadAndCompareNextT(
                        curSortFieldName, tListItemIdx, tList,
                        curSortFieldNamesIdx, sortFieldNamesIdxStartPrinting,
                        nextTValueStrings)){
                    // Advance tItem to nextT (tListItem mismatches nextT)
                    break;
                }

            }   // else no nextT
        }
    }

    /**
     * @param fieldName
     * @param tListItemIdx
     * @param tList
     * @param sortFieldNamesIdx
     * @param sortFieldNamesIdxStartPrinting
     * @param nextTValueStrings
     * @param <T>
     *
     * @return true if tListItem mismatches nextT
     */
    static private <T> boolean printItemField_LoadAndCompareNextT(
            String fieldName, int tListItemIdx, final List<T> tList,
            int sortFieldNamesIdx, int sortFieldNamesIdxStartPrinting,
            List<String> nextTValueStrings){

        final String tItemFieldString;
        final String nextTFieldString;
        try{
            final T tItem = tList.get(tListItemIdx);
            tItemFieldString = tItem.getClass().getField(fieldName).get(tItem).toString();
            if(sortFieldNamesIdx >= sortFieldNamesIdxStartPrinting){
                if(tListItemIdx > 0){
                    System.out.print(", ");
                }
                /*
                 * Print tItemFieldString */
                System.out.print(tItemFieldString);
            }

            // Load nextTFieldString into nextTValueStrings
            final T nextT = tList.get(tListItemIdx + 1);
            final Field nextTSortField = nextT.getClass().getField(fieldName);
            nextTFieldString = nextTSortField.get(nextT).toString();
            /*
             * Load nextTFieldString into nextTValueStrings */
            nextTValueStrings.add(nextTFieldString);

        } catch(    NoSuchFieldException | NullPointerException | SecurityException
            |   IllegalAccessException | IllegalArgumentException | ExceptionInInitializerError e){
            throw new RuntimeException(
                "Error getting field (with value " + fieldName + " in sortFieldNames), "
                    + "for sort printing.",
                e);
        }

        /*
         * Compare */
        return ! tItemFieldString.equalsIgnoreCase(nextTFieldString);
    }

}
