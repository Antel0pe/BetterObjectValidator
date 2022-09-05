import javax.crypto.interfaces.PBEKey;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;


public class TestClass{

    public static void main(String[] args) {
        ObjectToValidate objectToValidate = ObjectToValidate.builder()
                .num(1)
                .listOfNums(List.of(1))
                .build();

        ObjectToValidateValidator objectToValidateValidator = ObjectToValidateValidator.builder()
                .num(equalTo(1))
                .listOfNums(contains(2))
                .build();

        objectToValidateValidator.validate(objectToValidate);

    }
}