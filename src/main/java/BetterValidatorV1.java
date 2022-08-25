import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matcher;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;

public abstract class BetterValidatorV1<T extends BetterValidatorV1<T>> {
    protected Map<String, List<Matcher<? super Object>>> matchers;
    protected String asserts;

    public BetterValidatorV1(){
        matchers = new HashMap<>();
    }

    public T asserts(String message){
        this.asserts = message;
        return getThis();
    }

    public T build(){
        return getThis();
    }

    public abstract T getThis();



    public void validate(ObjectToValidate obj){
        for (String key: matchers.keySet()) {
            try {
                for(Matcher<? super Object> m: matchers.get(key))
                    assertThat(String.format("Property '%s' is not valid", key),
                            PropertyUtils.getProperty(obj, key), m);
            } catch (AssertionError ae){
                System.out.println("Assertion failed");
            } catch (Exception e){
                System.out.println("General exception");
            }
        }
    }

    public void generateClass(){
        final String validationClass = "MockValidationClass";
        final String parentClass = "MockParentClass";

        try (FileWriter writer = new FileWriter(validationClass)){
            List<List<String>> code = getCode(validationClass, parentClass);

            for(List<String> section: code){
                for(String line: section){
                    writer.write(line);
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e){
            System.out.println("IOException");
        }
    }

    public List<List<String>> getCode(String validationClass, String parentClass){
        List<List<String>> result = new ArrayList<>(
                List.of(
                        getImports(""),
                        List.of("\n\n"),
                        getClassDeclaration("", validationClass, parentClass),
                        List.of("\n"),
                        getBoilerplateMethods("\t", validationClass),
                        List.of("\n")));

        result.addAll(getFieldBuilderMethods("\t", validationClass));
        result.add(List.of("\n"));
        result.add(List.of("}"));

        return result;
    }

    public List<String> getImports(String offset){
        List<String> result = new ArrayList<>();

        result.add(offset + "import org.hamcrest.Matcher;");

        return result;
    }

    public List<String> getClassDeclaration(String offset, String validationClass, String parentClass){
        return List.of(
                offset + "public class " + validationClass + " extends " + parentClass + "{");
    }

    public List<String> getBoilerplateMethods(String offset, String validationClass){
        return List.of(
                String.format("%spublic %s () { super(); }\n", offset, validationClass), //todo want a parent class?
                String.format("%spublic static %s builder() { return new %s(); }\n", offset, validationClass, validationClass),
                String.format("%s@Override\n", offset),
                String.format("%spublic %s getThis() { return this; }\n", offset, validationClass)
        );
    }


    //todo not returning anything
    public List<List<String>> getFieldBuilderMethods(String offset, String validationClass){
        List<List<String>> result = new ArrayList<>();

        for (String key: matchers.keySet()){
            List<String> method = new ArrayList<>();

            method.add(String.format("%s@SafeVarargs", offset));
            method.add(String.format("%spublic %s %s(Matcher<? super Object>... match){\n", offset, validationClass, key));
            method.add(String.format("%s\tfor(Matcher<? super Object> m: match)", offset));
            method.add(String.format("%s\t\tthis.matchers.put(\"%s\", m);\n", offset, key));
            method.add(String.format("%s\treturn this;\n", offset));
            method.add(String.format("%s}\n", offset));

            result.add(method);
            result.add(List.of("\n"));
        }

        return result;
    }

}
