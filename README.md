# Better Object Validator

This testing framework is specifically designed to help create large complex assertions, modify them on an assertion by assertion basis, and reuse them as needed.

First let's say this is a class we want to test:

```java
@Validator
public class TestObject {
  private int num;
  private String str;
  private List<Boolean> booleanList;
  private Map<Integer, String> integerStringMap;
}
```
We've annotated this class with "@Validator" which will generate a sub class that can be used like:


```java
public static void main(String[] args) {
  TestObject obj = TestObject.builder()
                .word("hello")
                .num(3)
                .listOfNums(List.of(1, 2))
                .mapOfStrings(Map.of("1", "1"))
                .build();


  TestObjectValidator validator = TestObjectValidator
                .builder()
                .word(containsString("h"))
                .num(equalTo(3), notNullValue())
                .listOfNums(contains(1, 2))
                .mapOfStrings(hasEntry("1", "1"))
                .asserts("Error message to be displayed if this assertion fails")
                .validate(obj);
}

```
Some things to point out:
- The generated class is the annotated class name + Validator
- It has all the same field names and takes Hamcrest matchers that apply to the field types
- The actual assertion bit happens when you call validate() on the TestObject
- You can set any of these fields after construction using setters and re-validate your object 


Todos:
- Better README with more details on use and exceptions
- Add to Maven Central
- Tests
- Github actions to release versions