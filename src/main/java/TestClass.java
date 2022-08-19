import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public class TestClass{

    public static void main(String[] args) {
        ObjectToValidate objectToValidate = ObjectToValidate.builder()
                .num(3)
                .word("word")
                .listOfNums(List.of(1, 2, 3))
                .mapOfStrings(Map.of(
                        "k1", List.of("v1", "v2"),
                        "k2", List.of("v3", "v4")))
                .build();


    }
}