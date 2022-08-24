import lombok.Getter;
import org.hamcrest.Matcher;

import java.util.*;

@Getter
public class ValidationObject extends BetterValidatorV1<ValidationObject>{
    private String word;
    public int num;

    private List<Integer> listOfNums;
    private Map<String, List<String>> mapOfStrings;

    public ValidationObject(){
        super();

        listOfNums = new ArrayList<>();
        mapOfStrings = new HashMap<>();
    }

    public static ValidationObject builder(){
        return new ValidationObject();
    }

    @Override
    public ValidationObject getThis(){
        return this;
    }



    @SafeVarargs
    public final ValidationObject num(Matcher<? super Object>... matchers){
        for(Matcher<? super Object> m: matchers)
            this.matchers.getOrDefault("num", new ArrayList<>()).add(m);
        return this;
    }

    @SafeVarargs
    public final ValidationObject word(Matcher<? super Object>... matchers){
        for(Matcher<? super Object> m: matchers)
            this.matchers.getOrDefault("word", new ArrayList<>()).add(m);
        return this;
    }
}
