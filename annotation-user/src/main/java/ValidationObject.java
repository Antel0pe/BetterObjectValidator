import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ValidationObject extends ValidatorBase<ValidationObject> {
    private String word;
    public int num;

    public ValidationObject(){
        super();
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
        Matcher<? super Object> m = equalTo(3);
        assertThat(3, m);
        this.matchers.get("num").addAll(List.of(matchers));
        return this;
    }

    @SafeVarargs
    public final ValidationObject word(Matcher<? super Object>... matchers){
        this.matchers.get("word").addAll(List.of(matchers));
        return this;
    }
}
