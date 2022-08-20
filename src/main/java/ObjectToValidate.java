import lombok.Getter;
import lombok.ToString;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter //todo requires getter
@ToString
public class ObjectToValidate extends BetterValidatorV1{
    private String word;
    public int num;

    private List<Integer> listOfNums;
    private Map<String, List<String>> mapOfStrings;

    public ObjectToValidate(){
        super();

        listOfNums = new ArrayList<>();
        mapOfStrings = new HashMap<>();
    }

    public static ObjectToValidate builder(){
        return new ObjectToValidate();
    }

    public ObjectToValidate build(){
        return this;
    }

    public ObjectToValidate num(int num){
        this.num = num;
        return this;
    }

    public ObjectToValidate num(Matcher m){
        this.matchers.put("num", m);
        return this;
    }

    public ObjectToValidate word(String word){
        this.word = word;
        return this;
    }

    public ObjectToValidate listOfNums(List<Integer> listOfNums){
        this.listOfNums = listOfNums;
        return this;
    }

    public ObjectToValidate mapOfStrings(Map<String, List<String>> mapOfStrings){
        this.mapOfStrings = mapOfStrings;
        return this;
    }
}
