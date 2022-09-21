import com.squareup.javapoet.*;
import lombok.AllArgsConstructor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.hamcrest.Matcher;


public class TemplateGenerator {

    @Getter
    @AllArgsConstructor
    public static class TemplateInput{
        private final String className;
        private List<MethodInput> methods;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MethodInput{
        private String fieldName;
        private String matcherType;
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
                .addMethods(templateInput.getMethods().stream()
                        .map(f -> addFunction(clazzName, f))
                        .collect(Collectors.toList()))
                .build();

        JavaFile javaFile = JavaFile.builder("", validatorType).build();

        //javaFile.writeTo(System.out);
        try {
            javaFile.writeTo(filer);
        } catch (IOException e){
            throw e;
        }

    }

    private void preprocessInput(TemplateInput templateInput){
        for (MethodInput input: templateInput.getMethods()){
            ClassName inputClass = ClassName.get("", input.getMatcherType());

            if (isPrimitiveType(inputClass)){
                input.setMatcherType("Object");
            }

        }
    }

    private boolean isPrimitiveType(TypeName clazz){
        return
                clazz.toString().equals(TypeName.BOOLEAN.toString()) ||
                clazz.toString().equals(TypeName.BYTE.toString()) ||
                clazz.toString().equals(TypeName.SHORT.toString()) ||
                clazz.toString().equals(TypeName.INT.toString()) ||
                clazz.toString().equals(TypeName.LONG.toString()) ||
                clazz.toString().equals(TypeName.CHAR.toString()) ||
                clazz.toString().equals(TypeName.FLOAT.toString()) ||
                clazz.toString().equals(TypeName.DOUBLE.toString());

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

    private MethodSpec addFunction(ClassName clazz, MethodInput methodInput){
        ParameterizedTypeName typeOfFieldParam = ParameterizedTypeName.get(
                ClassName.get(Matcher.class),
                WildcardTypeName.supertypeOf(
                        ClassName.get("", methodInput.getMatcherType())));

        return MethodSpec.methodBuilder(methodInput.getFieldName())
                .addAnnotation(SafeVarargs.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(clazz)
                .addParameter(ArrayTypeName.of(typeOfFieldParam), "m")
                .varargs(true)
                .addStatement("this.matchers.putAll($S, $T.of(m))", methodInput.getFieldName(), List.class)
                .addStatement("return this")
                .build();

    }





}
