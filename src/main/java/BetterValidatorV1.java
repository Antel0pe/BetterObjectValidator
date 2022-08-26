import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.samskivert.mustache.Mustache;
import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matcher;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class BetterValidatorV1<T extends BetterValidatorV1<T>> {
    protected Multimap<String, Matcher<? super Object>> matchers;
    protected String asserts;

    public BetterValidatorV1(){
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

    public void generateClass(ObjectToValidate obj) throws IntrospectionException, IOException {
        final String pathToTemplate = "src/main/java/";
        final String template = "GeneratedClassTemplate.mustache";
        final String className = "MockValidatorClass";


        List<String> fields = Arrays.stream(Introspector.getBeanInfo(obj.getClass()).getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(s -> !s.equalsIgnoreCase("class")).toList();

        FileReader file;
        FileWriter writer;
        try {
            file = new FileReader(pathToTemplate + template);
            writer = new FileWriter(className + ".java");
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        String code = Mustache.compiler().compile(file).execute(Map.of("class", className, "fields", fields));

        writer.write(code);

        file.close();
        writer.close();


    }

}
