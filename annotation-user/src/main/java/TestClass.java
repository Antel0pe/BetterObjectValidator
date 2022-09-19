import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;


public class TestClass {

    public static void main(String[] args) {
        ObjectToValidate obj = ObjectToValidate.builder()
                .word("hello")
                .num(3)
                .listOfNums(List.of(1, 2))
                .mapOfStrings(Map.of("1", List.of("1")))
                .build();


        ObjectToValidateValidator validator = ObjectToValidateValidator.builder()
                .word(containsString("h"))
                .num(equalTo(4), notNullValue())
                .listOfNums(contains(1, 2))
                //.mapOfStrings(hasEntry("1", contains("1")))
                .asserts("validates big obj")
                .validate(obj);
    }


}