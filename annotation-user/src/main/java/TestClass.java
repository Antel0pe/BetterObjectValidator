//import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


//@Slf4j
public class TestClass{

    public static void main(String[] args) throws IntrospectionException, IOException {
//        ObjectToValidate objectToValidate = ObjectToValidate.builder()
//                .num(3)
//                .word("word")
//                .listOfNums(List.of(1, 2, 5))
//                .mapOfStrings(Map.of(
//                        "k1", List.of("v1", "v2"),
//                        "k2", List.of("v3", "v4")))
//                .build();
//
//        ValidationObject validationObject = ValidationObject.builder()
//                .asserts("validation obj is correct")
//                .num(equalTo(3), notNullValue())
//                .word(equalTo("word"))
//                .build();

        //log.error("something went wrong");
        Logger logger = LoggerFactory.getLogger("SampleLogger");
        logger.info("Hi This is my first SLF4J program");
        ValidationObject validationObject = new ValidationObject();

        BetterValidatorProcessor betterValidatorProcessor = new BetterValidatorProcessor();

        //objectToValidate.getListOfNums();


//        ObjectToValidateValidator validateValidator = com.BetterValidator.ObjectToValidateValidator.builder()
//                .num(equalTo(2), notNullValue())
//                .build();
//
//
//        validateValidator.validate(objectToValidate);

    }
}