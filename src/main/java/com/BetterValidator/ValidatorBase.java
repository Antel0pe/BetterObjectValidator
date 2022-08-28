package com.BetterValidator;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class ValidatorBase<T extends ValidatorBase<T>> {
    protected Multimap<String, Matcher<? super Object>> matchers;
    protected String asserts;

    public ValidatorBase(){
        matchers = MultimapBuilder.hashKeys().arrayListValues().build();
    }

    public T asserts(String message){
        this.asserts = message;
        return getThis();
    }

    public T build(){
        return getThis();
    }

    public abstract T getThis();



    public T validate(ObjectToValidate obj) {
        for (String key : matchers.keySet()) {
            try {
                for (Matcher<? super Object> m : matchers.get(key))
                    assertThat(String.format("Property '%s' is not valid", key),
                            PropertyUtils.getProperty(obj, key), m);
            } catch (AssertionError ae) {
                System.out.println("Assertion failed");
            } catch (Exception e) {
                System.out.println("General exception");
            }
        }

        return (T) getThis();
    }


}
