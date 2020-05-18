###### **USAGE**
- Just 1 call to `SortUtil#sortTopLevelValueObjectList`
  - See its Javadoc method comment for further detailed usage/example
  - Params
    - `tList` is the List to be sorted
      - It will be sorted in that original List, as well as returned, and printed
        (see last "printing" bullet below in this USAGE section)
      - That List's [parametrized] type of Objects can be any arbitrary Object type that meets these requirements:
        - Fields to be sorted on are all top-level
        - Those fields must all be primitives, Boxed Primitives, or Strings
    - `sortFieldNamePrecedences` is a List of Strings of the field names (in Objects-to-sort) to sort by
      - Sort precedence is with respect to the order in that List
      - All sorts are _ascending by default_, except for those field names that are prepended/flagged with a `"-"` dash
        (e.g: `"-nameToSortDescending"`)
      - All listed field names must be non-blank, valid, top-level, `public` variable names
        (matching spelling & character casing, without whitespace) in the Objects-to-sort
        
- Example Usage
  - Given `<T>` Object-to-sort as...
      ```
       class SomeObject{
         public String name0;
         public int name1;
         public Integer name2;
       }
       ```
  - ... with a `sortFieldNamePrecedences` param of `{"-name2", "name0", "-name1"}`
  - ... then sorting will be made by
    (first) `name2` descending, (second) `name0` ascending, (third) `name1` descending
    
- Printing in `SortUtil#printSortedListWithHeaderFooter`
  - Does not increase overall algorthmic complexity (see section below)
  - Implemented somewhat different than we discussed by email, but I think should still be sufficient
  - I implemented in a way to better aid me in development/debugging
  - Objects are printed 1-per-line in their sorted order
  - Fields printed on a given Object line are only those that were necessary to compare to the sorted-adjacent Objects
    - They are printed in order of precedence
    - They are just the fields that match adjacent Objects (immediately before and after),
      up until the first non-match (for which comparison during sorting was able to stop after)
      
- `Maven usage`
  - Install `DrFirst` JAR in repository
    - i.e: using mvn install:install-file (https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html)
  - Depend Maven project on `DrFirst` JAR (https://github.com/cellepo/JavaSortAnyTopLevelValueObject/releases),
    in project `pom`:
    ```
    <dependency>
     <groupId>DrFirst</groupId>
      <artifactId>chrisellepola</artifactId>
     <version>1.0</version>
    </dependency>
    ```

###### **Algorithmic Complexity**
- The main sorting algorithm entry is `SortUtil#sortTopLevelValueObjectList`, so this is Big-O analysis of that method
- Let `t` be the number of Objects to be sorted (in `tList` param),
  and `s` be the number of sort field parameters/precedences (in `sortFieldNamePrecedences` param)
  - _Runtime:_ `O(t*log(t)*s)`
    - Algorithm calls `List#sort`, which itself is log-linear `O(t*Log(t))`,
      but the Comparator provided in this algorithm is itself linear `O(s)` for a given comparison call
      (of which there are `log(t)` for each `t`). So the overall runtime is **`O(t*log(t)*s)`**
    - log-linear `O(t*log(t))` is normally optimal for sorting in general -
       but that is when there are not multiple sort precedences/fields.
      In this sorting, since there can be `s` multiple precedences/parameters to fall-through sorting on
      (for ties on prior precedences), each comparison takes `s` operations in a worst case
      (instead of just 1, when there is typically just 1 sort field parameter).
      So I believe `O(t*log(t))` is optimal here
    - Even though `#sortTopLevelValueObjectList` makes 1 call to `#printSortedListWithHeaderFooter` (before returning),
      and the latter is not a trivial algorithm itself, `printSortedListWithHeaderFooter` is nonetheless just `O(t*s)`
      (a print op for each `s` is done for each `t` in a worst case).
      So its contribution does not increase the overall runtime of `#sortTopLevelValueObjectList`.
  - _Space:_ `O(s)`
    - Other than constant-space `O(1)` storages throughout the algorithm,
      `O(s)` comes from the worst case of `SortUtil#printSortedList`
      (which is subsequently called once by main algorithm):
      A List of up to `s` field values is stored at time there
      (for comparing a given sorted-object's fields to its following sorted-object's fields,
       when all `s` fields are necessary in that comparison)  
    - For the sorting itself, `O(1)` constant space would instead be optimal.
      So in use-cases where that matters, it can easily be achieved by dispensing away with this type of printing -
      it is technically not necessary for real production runtime,
      and was implemented instead to aid in development/debugging

###### **Further improvements - Stretch Goals**
- Consider implementing handling comparison of nested field values (below top-level)
  - That could have a 1st iteration of handling nesting in Collections,
    and a 2nd iteration of handling nesting in arbitrary Objects (other than Collections, Boxed Primitives, & Strings)
- Iterate a refactor using Java 8 functional programming Lambdas to reduce some copied code (particularly testing code)
- If possible, implement `#compareByField` to successfully do checked casts,
   without requiring `@SuppressWarnings("unchecked")` in order to avoid Compilation Warning
   ("uses unchecked or unsafe operations").  I think the generic parameter (`Comparable<Object>`) confounds that.