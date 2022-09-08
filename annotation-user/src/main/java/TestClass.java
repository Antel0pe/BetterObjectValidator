import org.hamcrest.Matcher;

import javax.crypto.interfaces.PBEKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TestClass{

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



    }

}