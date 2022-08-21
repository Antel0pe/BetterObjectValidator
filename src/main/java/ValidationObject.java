import lombok.Getter;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ValidationObject extends BetterValidatorV1{
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

    public ValidationObject build(){
        return this;
    }

    public ValidationObject num(Matcher m){
        this.matchers.put("num", m);
        return this;
    }

    public ValidationObject word(Matcher m){
        this.matchers.put("word", m);
        return this;
    }
}
