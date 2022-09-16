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
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
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
    @Setter
    @AllArgsConstructor
    public static class MethodInput{
        private String fieldName;
        private String matcherType;
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

    private void preprocessInput(TemplateInput templateInput){
        for (MethodInput input: templateInput.getFunctions()){
            ClassName inputClass = ClassName.get("", input.getMatcherType());

            if (isPrimitiveType(inputClass)){
                input.setMatcherType("Object");
            }
            //else if (inputClass.compareTo(ClassName.get(String.class)) == 0){
//                input.setMatcherType("java.util.List<java.lang.String>");
//            }

        }
    }

    private boolean isPrimitiveType(TypeName clazz){
        return clazz.toString().equals(TypeName.BOOLEAN.toString()) ||
                clazz.toString().equals(TypeName.BYTE.toString()) ||
                clazz.toString().equals(TypeName.SHORT.toString()) ||
                clazz.toString().equals(TypeName.INT.toString()) ||
                clazz.toString().equals(TypeName.LONG.toString()) ||
                clazz.toString().equals(TypeName.CHAR.toString()) ||
                clazz.toString().equals(TypeName.FLOAT.toString()) ||
                clazz.toString().equals(TypeName.DOUBLE.toString());

    }

    public void generateClass(Filer filer, TemplateInput templateInput) throws IOException {
        ClassName clazzName = ClassName.get("", templateInput.getClassName());

        preprocessInput(templateInput);

        TypeSpec validatorType = TypeSpec.classBuilder(templateInput.getClassName())
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(ValidatorBase.class),
                        clazzName))
                .addModifiers(Modifier.PUBLIC)
                .addMethods(boilerPlate(clazzName))
                .addMethods(templateInput.getFunctions()
                        .stream()
                        .map(f -> addFunction(clazzName, f))
                        .collect(Collectors.toList()))
                .build();

        JavaFile javaFile = JavaFile.builder("", validatorType).build();

        //javaFile.writeTo(System.out);
        javaFile.writeTo(filer);

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
                .addStatement("this.matchers.putAll($S, $T.of(m))", methodInput.getFieldName(), List.class)
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



    public void test(Filer filer) throws IOException {
        MethodInput methodInput = new MethodInput("field", "String");



        TypeSpec test = TypeSpec.classBuilder("test")
                .superclass(ParameterizedTypeName.get(
                        ClassName.get(ValidatorBase.class),
                        ClassName.get("", "test")))
                .addModifiers(Modifier.PUBLIC)
                .addMethods(boilerPlate(ClassName.get("", "test")))
                .addMethod(addFunction(ClassName.get("", "test"), methodInput))
                .build();

        JavaFile javaFile = JavaFile.builder("", test).build();

        javaFile.writeTo(System.out);
        javaFile.writeTo(filer);


    }


}
