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
        this.matchers.get("num").addAll(List.of(matchers));
        return this;
    }

    @SafeVarargs
    public final ValidationObject word(Matcher<? super Object>... matchers){
        this.matchers.get("word").addAll(List.of(matchers));
        return this;
    }
}
