package com.BetterValidator;

import com.google.auto.service.AutoService;
import com.samskivert.mustache.Mustache;
import lombok.Cleanup;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.BetterValidator.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class BetterValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env){
        try {
            FileWriter writer = new FileWriter(new File("hello.java"));
            writer.write("hello");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // annotations should be size 1

//        for (TypeElement a: annotations){
//            Set<? extends Element> annotatedClasses = env.getElementsAnnotatedWith(a);
//
//            if (annotatedClasses.isEmpty()) continue;
//
//
//            assertThat("how many annotated elements", annotatedClasses, hasSize(1));
//            String className = annotatedClasses.stream().toList().get(0).getSimpleName().toString();
//
//            try {
//                //generateClass(Class.forName(String.valueOf(a.getQualifiedName())));
//                generateClass(annotatedClasses.stream().toList().get(0).getClass());
//            } catch (IntrospectionException | IOException e) {
//                e.printStackTrace();
//            }
//        }

        return true;
    }

    public void generateClass(Class<?> clazz) throws IntrospectionException, IOException {
        final String pathToTemplate = "src/main/java/";
        final String template = "src/main/java/GeneratedClassTemplate.mustache";
        final String className = clazz.getName() + "com.BetterValidator.Validator";


        List<String> fields = Arrays.stream(Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(s -> !s.equalsIgnoreCase("class")).toList();

        FileReader file = new FileReader(template);

        JavaFileObject generatedFile = processingEnv.getFiler().createSourceFile(className);
        @Cleanup PrintWriter writer = new PrintWriter(generatedFile.openWriter());

        String code = Mustache.compiler().compile(file).execute(Map.of("class", className, "fields", fields));
        writer.print(code);

    }
}
