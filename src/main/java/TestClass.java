import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class TestClass{

    public static void main(String[] args) {
        ObjectToValidate objectToValidate = ObjectToValidate.builder()
                //.num(3)
                .word("word")
                .listOfNums(List.of(1, 2, 3))
                .mapOfStrings(Map.of(
                        "k1", List.of("v1", "v2"),
                        "k2", List.of("v3", "v4")))
                .build();

        ObjectToValidate validator = ObjectToValidate.builder()
                .num(equalTo(3))
                .build();

        System.out.println(validator.toString());

        //System.out.println(objectToValidate.getNum());

        //objectToValidate.getNonNullFields();
        //objectToValidate.getSetFields();
        //validator.getSetFields();
        validator.getSetFields(objectToValidate);


    }
}