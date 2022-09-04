import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.List;
import java.util.Set;


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



            String className = annotatedClasses.stream().toList().get(0).getSimpleName().toString();
            List<String> fields = annotatedClasses.stream().toList().get(0)
                    .getEnclosedElements()
                    .stream()
                    .map(Object::toString)
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


}
