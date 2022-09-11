import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import org.hamcrest.Matcher;


import javax.crypto.interfaces.PBEKey;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TestClass {

    public static void main(String[] args) {
        ObjectToValidate objectToValidate = ObjectToValidate.builder()
                .num(1)
                .listOfNums(List.of(1))
                .build();


        ObjectToValidateValidator objectToValidateValidator = ObjectToValidateValidator.builder()
                .num(equalTo(1))
                .listOfNums(contains(1))
                .build();


        objectToValidateValidator.validate(objectToValidate);
        //assertThat((java.util.List<String>) ((Object) List.of(1)), contains("3"));


    }

    @FunctionalInterface
    public interface MatchingAssertion {
        abstract <T> Optional<String> match(T field);
    }

    public <T> Optional<String> helper(T field, Matcher<? super T> match) {
        try {
            assertThat("something", field, match);
        } catch (Exception e){
            return Optional.of(e.toString());
        }

        return Optional.empty();
    }

    public static class Matching {
        List<Matcher<? super List<String>>> fieldName;
        List<Matcher<? super Object>> secondField;

        public List<Matcher<? super List<String>>> fieldName() {
            return fieldName;
        }



    }


}


























}