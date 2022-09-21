import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@Validator
public class TestObject {
    private int num;
    private String str;
    private List<Boolean> booleanList;
    private Map<Integer, String> integerStringMap;
    private double aDouble;

}
