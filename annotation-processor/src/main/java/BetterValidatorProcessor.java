import com.google.auto.service.AutoService;
import org.checkerframework.checker.signature.qual.ClassGetName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
            List<? extends Element> annotatedClasses = env.getElementsAnnotatedWith(a).stream().toList();

            //if (annotatedClasses.isEmpty()) continue;

            assertThat("should have only 1 annotated class", annotatedClasses, hasSize(1));
            Element clazz = annotatedClasses.get(0);

            String className = clazz.getSimpleName().toString() + "Validator";
            List<TemplateGenerator.MethodInput> templateInput = new ArrayList<>();

            for(Element e: clazz.getEnclosedElements()){
                if (!e.toString().contains("(")){
                    String fieldName = e.toString();
                    String matcherType = "String";//processingEnv.getTypeUtils().asElement((TypeMirror) e).toString();

                    templateInput.add(new TemplateGenerator.MethodInput(fieldName, matcherType));
                }
            }

            try {
                templateGenerator.generateClass(getClass().getClassLoader(), processingEnv.getFiler(), className, templateInput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


}
