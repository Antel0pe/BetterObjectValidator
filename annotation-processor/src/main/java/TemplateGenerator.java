import com.samskivert.mustache.Mustache;
import com.squareup.javapoet.*;
import lombok.AllArgsConstructor;
import lombok.Cleanup;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Parameterizable;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.*;

import lombok.Getter;
import org.hamcrest.Matcher;
import org.testng.annotations.Test;


public class TemplateGenerator {
    private static final String templateFilePath = "GeneratedClassTemplate.mustache";
    private static final String templatePath = "GeneratedClassTemplate.mustache";

    @Getter
    @AllArgsConstructor
    public static class TemplateInput{
        private final String className;
        private List<MethodInput> functions;
    }

    @Getter
    @AllArgsConstructor
    public static class MethodInput{
        private final String fieldName;
        private final String matcherType;
    }

    public void generateClass(ClassLoader classLoader,
                              Filer filer,
                              String className,
                              List<MethodInput> vals) throws IOException {
        //final String generatedClassName = className + "Validator";


        InputStreamReader templateFile = new InputStreamReader(
                Objects.requireNonNull(classLoader.getResourceAsStream(templatePath)));
        JavaFileObject generatedFile = filer.createSourceFile(className);
        @Cleanup PrintWriter writer = new PrintWriter(generatedFile.openWriter());

        String code = Mustache.compiler().compile(templateFile).execute(new Object() {
            Object funcs = vals;
            Object clazz = className;
        });

        writer.print(code);

    }

    public void generateClass(Filer filer, TemplateInput templateInput) throws IOException {



    }

    private MethodSpec addFunction(ClassName clazz, MethodInput methodInput){
        ParameterizedTypeName inputParam = ParameterizedTypeName.get(
                ClassName.get(Matcher.class),
                WildcardTypeName.supertypeOf(ClassName.get("", methodInput.getMatcherType())));

        MethodSpec method = MethodSpec.methodBuilder(methodInput.getFieldName())
                .addAnnotation(SafeVarargs.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(clazz)
                .addParameter(ArrayTypeName.of(inputParam), "m")
                .varargs()
                .addStatement("this.matchers.putAll(List.of(m))")
                .addStatement("return this")
                .build();

        return method;
    }

    public Iterable<MethodSpec> boilerPlate(ClassName clazz){

        return new ArrayList<>(List.of(
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("super()")
                        .build(),

                MethodSpec.methodBuilder("builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(clazz)
                        .addStatement("return new $T()", clazz)
                        .build(),

                MethodSpec.methodBuilder("getThis")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(clazz)
                        .addStatement("return this")
                        .build()

                ));

    }



    @Test
    public void test() throws IOException {
        MethodInput methodInput = new MethodInput("field", "String");


        TypeSpec test = TypeSpec.classBuilder("test")
                .addModifiers(Modifier.PUBLIC)
                .addMethods(boilerPlate(ClassName.get("", "test")))
                .addMethod(addFunction(ClassName.get("", "test"), methodInput))
                .build();

        JavaFile javaFile = JavaFile.builder("test", test).build();

        javaFile.writeTo(System.out);


    }


}
