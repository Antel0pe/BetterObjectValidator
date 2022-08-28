package com.BetterValidator;

import lombok.Getter;
import org.hamcrest.Matcher;

import java.util.List;


@Getter
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
        this.matchers.get("num").addAll(List.of(matchers));
        return this;
    }

    @SafeVarargs
    public final ValidationObject word(Matcher<? super Object>... matchers){
        this.matchers.get("word").addAll(List.of(matchers));
        return this;
    }
}
