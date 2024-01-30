# Better Object Validator

This testing framework is designed to help bundle related assertions, modify them on an case by case basis, and reuse them as needed.

    
First let's say this is a class we want to test:

```java
@Validator
@Builder
public class TestObject {
  private String word;
  private int num;
  private List<Integer> listOfNums;
  private Map<String, String> mapOfStrings;
}
```
We've annotated this class with "@Validator" which will generate a validator class that can be used like:


```java
public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
                .word("hello")
                .num(3)
                .listOfNums(List.of(1, 2))
                .mapOfStrings(Map.of("1", "1"))
                .build();


  TestObjectValidator validator = TestObjectValidator.builder()
                .word(isEqual("hello"))
                .num(equalTo(3), notNullValue())
                .listOfNums(contains(1, 2))
                .mapOfStrings(hasEntry("1", "1"))
                .asserts("Error message to be displayed if this assertion fails");

  validator.validate(objectToValidate);
}

```
The automatically generated testing class called, {YourClassName}Validator, enables you to assert the values of all fields in your class using builder pattern. For each field you specify, you input 1 or more Hamcrest matchers to specify what are valid values.  

In the example above we are specifying that:
1. objectToValidate.word is equal to "hello"
2. objectToValidate.num is not null and equals 3
3. objectToValidate.listOfNums contains a 1 and 2
4. objectToValidate.mapOfStrings has an entry with key 1 and value 1

In the case that any of these assertions are not true, the validator class will let us know what assertion did not pass along with the custom error message we have specified. 

This on its own isn't all that special, after all you can do the same thing in any number of Java testing frameworks.
```java
public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
                .word("hello")
                .num(3)
                .listOfNums(List.of(1, 2))
                .mapOfStrings(Map.of("1", "1"))
                .build();

  assertTrue(objectToValidate.word.equals("hello"));
  assertEquals(3, objectToValidate.num);
  assertTrue(objectToValidate.listOfNums.contains(1));
  assertTrue(objectToValidate.listOfNums.contains(2));
  assertTrue(objectToValidate.mapOfStrings.containsKey("1"));
  assertEquals("1", objectToValidate.mapOfStrings.get("1"));
}
```
Where I feel this method really stands out is in reusability.  

Let's say we take our test object and call a method that changes the value of the word field.

```java
public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
                .word("hello")
                //...
                .build();

  objectToValidate.changeWordTo("bye");
}

```

Well then it's easy enough to write another assert.

```java
assertTrue(objectToValidate.word.equals("hello"));

objectToValidate.changeWordTo("bye");

assertTrue(objectToValidate.word.equals("bye"));

```
But what if we want to assert that this method doesn't have any unintended side effects? What if we want to make sure the rest of the class state has not changed? That's pretty easy, just copy the rest of the asserts from above - or better yet put them all in their own function and call the function of asserts. 

```java
public static functionWithAsserts(TestObject objectToValidate){
  assertEquals(3, objectToValidate.num);
  assertTrue(objectToValidate.listOfNums.contains(1));
  assertTrue(objectToValidate.listOfNums.contains(2));
  assertTrue(objectToValidate.mapOfStrings.containsKey("1"));
  assertEquals("1", objectToValidate.mapOfStrings.get("1"));
}


public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
                .word("hello")
                .num(3)
                .listOfNums(List.of(1, 2))
                .mapOfStrings(Map.of("1", "1"))
                .build();

  // assert class state before
  assertTrue(objectToValidate.word.equals("hello"));
  functionWithAsserts(objectToValidate);

  // change value of word
  objectToValidate.changeWordTo("bye");

  // assert class state after
  assertTrue(objectToValidate.word.equals("bye"));
  functionWithAsserts(objectToValidate);
}
```

A little bit less clean, but seems to work well enough. Now what if we have another method that changes the value of a field and we want to make sure **it** doesn't have any side effects. Simple enough, we can extract the relevant assert from our function with asserts and proceed as we did above.

```java
public static functionWithAsserts(TestObject objectToValidate){
  // EXTRACTING THIS ASSERT
  //assertEquals(3, objectToValidate.num);

  assertTrue(objectToValidate.listOfNums.contains(1));
  assertTrue(objectToValidate.listOfNums.contains(2));
  assertTrue(objectToValidate.mapOfStrings.containsKey("1"));
  assertEquals("1", objectToValidate.mapOfStrings.get("1"));
}


public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
                .word("hello")
                .num(3)
                .listOfNums(List.of(1, 2))
                .mapOfStrings(Map.of("1", "1"))
                .build();

  // assert class state before
  assertTrue(objectToValidate.word.equals("hello"));
  assertEquals(3, objectToValidate.num);
  functionWithAsserts(objectToValidate);

  // change value of num
  objectToValidate.changeNumTo(5);

  // assert class state after
  assertTrue(objectToValidate.word.equals("hello"));
  assertEquals(5, objectToValidate.num);
  functionWithAsserts(objectToValidate);
}  
```

This isn't the best example in the world, but I hope that we can see that as we add more functions to test, it becomes harder to test the state of the class. We would repeat a lot of code, it would get very noisy, it's hard to tell exactly what we are asserting is changing.

What this framework does is create an object to organize all the relevant assertions AND give you the ability to change 1 at a time while maintaining all the previous ones.

```java
public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
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
                .asserts("Error message to be displayed if this assertion fails");

  validator.validate(objectToValidate);

  
  objectToValidate.changeWord("bye");
  validator.builder()
      .word(equalTo("bye"))
      .validate();

  objectToValidate.changeNum("5");
  validator.builder()
      .num(equalTo(5))
      .validate();

  objectToValidate.changeList(List.of(3,4));
  validator.builder()
      .listOfNums(contains(3, 4))
      .validate();
}

```
Each time we modify our class state, we make a subsequent call to our validator object to replace our assertion(s). When we call validator.validate(), it checks our updated assertion AND all the assertions we've made for every other field at once.  

So in the last "chunk" of the above code snippet where we change the value of objectToValidate.list, our validator object is checking the newly updated list field AND the word field AND the num field AND the map field without repeating any code. 

At a glance you can easily tell how the class should be changing after each function call while maintaining what the rest of the class state should be.

This could be particularly useful with integration tests like:

```java
public static void main(String[] args) {
  TestObject objectToValidate = TestObject.builder()
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
                .asserts("Error message to be displayed if this assertion fails");

  // Validate original state of objectToValidate
  validator.validate(objectToValidate);

  
  objectToValidate.step1(1, "abc");
  validator.builder()
      .word(contains("a"))
      .num(lessThan(5))
      .validate();

  objectToValidate.step2(List.of(1, 2, 3));
  validator.builder()
      .num(equalTo(19))
      .listOfNums(contains(3, 4))
      .validate();

  objectToValidate.step3();
  validator.validate();
}

```

I thought this was a cool idea while doing some automation testing at an internship and figured I'd explore building it! :smile::smile:

Todos:
- [ ] Add to Maven Central
- [ ] Tests
- [ ] Github actions to release versions
