import com.google.auto.service.AutoService;
import com.samskivert.mustache.Mustache;
import lombok.Cleanup;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SupportedAnnotationTypes("Validator")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class BetterValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env){
        // annotations should be size 1

        for (TypeElement a: annotations){
            Set<? extends Element> annotatedClasses = env.getElementsAnnotatedWith(a);

            if (annotatedClasses.isEmpty()) continue;

            assertThat("how many annotated elements", annotatedClasses, hasSize(1));
            Element element = annotatedClasses.stream().toList().get(0);



            try {
                generateClass(Class.forName(String.valueOf(a.getQualifiedName())));
            } catch (IntrospectionException | IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    public void generateClass(Class<?> clazz) throws IntrospectionException, IOException {
        final String pathToTemplate = "src/main/java/";
        final String template = "src/main/java/GeneratedClassTemplate.mustache";
        final String className = clazz.getName() + "Validator";


        List<String> fields = Arrays.stream(Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(s -> !s.equalsIgnoreCase("class")).toList();

        @Cleanup FileReader file = new FileReader(template);

        JavaFileObject generatedFile = processingEnv.getFiler().createSourceFile(className);
        @Cleanup PrintWriter writer = new PrintWriter(generatedFile.openWriter());

        String code = Mustache.compiler().compile(file).execute(Map.of("class", className, "fields", fields));
        writer.print(code);

    }
}
