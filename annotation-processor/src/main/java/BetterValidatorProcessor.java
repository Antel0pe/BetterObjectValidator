import com.google.auto.service.AutoService;
import com.samskivert.mustache.Mustache;
//import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SupportedAnnotationTypes("Validator")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class BetterValidatorProcessor extends AbstractProcessor {
    private final TemplateGenerator templateGenerator;

    public BetterValidatorProcessor(){
        templateGenerator = new TemplateGenerator();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env){

        for (TypeElement a: annotations){
            Set<? extends Element> annotatedClasses = env.getElementsAnnotatedWith(a);

            if (annotatedClasses.isEmpty()) continue;


            assertThat("how many annotated elements", annotatedClasses, hasSize(1));

            String className = annotatedClasses.stream().toList().get(0).getSimpleName().toString();
            List<String> fields1 = annotatedClasses.stream().toList().get(0)
                    .getEnclosedElements()
                    .stream()
                    .map(Object::toString)
                    .toList();
            List<String> fields = fields1.stream()
                    .filter(f -> !f.contains("("))
                    .toList();
            try {
                templateGenerator.generateClass(getClass().getClassLoader(), processingEnv.getFiler(), className, fields);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void generateClass(String className, List<String> fields) throws IntrospectionException, IOException {
        final String generatedClassName = className + "Validator";
        final String templatePath = "GeneratedClassTemplate.mustache";


        InputStreamReader templateFile = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(templatePath)));
        JavaFileObject generatedFile = processingEnv.getFiler().createSourceFile(generatedClassName);
        PrintWriter writer = new PrintWriter(generatedFile.openWriter());

        String code = Mustache.compiler().compile(templateFile).execute(Map.of("class", generatedClassName, "fields", fields));
        writer.print(code);
        writer.close();
    }
}
