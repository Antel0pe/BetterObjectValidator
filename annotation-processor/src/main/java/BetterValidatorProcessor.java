import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

            assertThat("should have only 1 annotated class",
                    annotatedClasses, hasSize(1));

            Element clazz = annotatedClasses.get(0);

            String className = clazz.getSimpleName().toString() + "Validator";
            List<TemplateGenerator.MethodInput> methodInput = new ArrayList<>();

            for(Element e: clazz.getEnclosedElements()){
                if (e.getKind().isField()){
                    String fieldName = e.toString();
                    String matcherType = e.asType().toString();

                    methodInput.add(new TemplateGenerator.MethodInput(fieldName, matcherType));
                }
            }

            try {
                templateGenerator.generateClass(processingEnv.getFiler(), new TemplateGenerator.TemplateInput(className, methodInput));

            } catch (IOException e) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.WARNING,
                        String.format("Could not generate validation file for %s", className));

                e.printStackTrace();
            }
        }

        return true;
    }


}
