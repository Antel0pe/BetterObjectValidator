import org.hamcrest.Matcher;

import java.util.HashMap;
import java.util.Map;

public class BetterValidatorV1 {
    protected Map<String, Matcher> matchers;

    public BetterValidatorV1(){
        matchers = new HashMap<>();
    }

}
