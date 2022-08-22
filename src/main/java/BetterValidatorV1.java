import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matcher;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class BetterValidatorV1 {
    //todo make matchers list of matchers
    protected Map<String, Matcher> matchers; //todo figure out generic params

    public BetterValidatorV1(){
        matchers = new HashMap<>();
    }

    public void validate(ObjectToValidate obj){
        for (String key: matchers.keySet()){
            try {
                assertThat(String.format("Property '%s' is not valid", key),
                        PropertyUtils.getProperty(obj, key), matchers.get(key));
            } catch (Exception e){}
        }
    }

    public void generateClass(){
        try (FileWriter writer = new FileWriter("MockValidationClass")){
            //getImports().forEach(l -> writer.write(l););
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e){
            System.out.println("IOException");
        }
    }

    public List<String> getImports(){
        List<String> result = new ArrayList<>();

        result.add("import org.hamcrest.Matcher;");

        return result;
    }

    public String getClassDeclaration(String validationClass, String parentClass){
        return "public class " + validationClass + " extends " + parentClass;
    }

    public List<String> getBoilerplateMethods(String validationClass){
        return List.of(
                String.format("public %s () { super(); }", validationClass), //todo want a parent class?
                String.format("public static %s builder() { return new %s(); }", validationClass, validationClass),
                String.format("public %s builder() { return this; }", validationClass)
        );
    }


    public List<List<String>> getFieldBuilderMethods(String validationClass){
        List<List<String>> result = new ArrayList<>();

        for (String key: matchers.keySet()){
            List<String> method = new ArrayList<>();

            method.add(String.format("public %s %s(Matcher<?> m){", validationClass, key));
            method.add(String.format("\tthis.matchers.put(\"%s\", m);", key));
            method.add(String.format("return this;"));

            result.add(method);
        }

        return result;
    }

}
