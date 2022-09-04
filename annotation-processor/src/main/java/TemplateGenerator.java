import com.samskivert.mustache.Mustache;
import lombok.AllArgsConstructor;
import lombok.Cleanup;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class TemplateGenerator {
    private static final String templateFilePath = "GeneratedClassTemplate.mustache";
    private static final String templatePath = "GeneratedClassTemplate.mustache";


    @AllArgsConstructor
    public static class ValidatorFunctions{
        public final String fieldName;
        public final String matcherType;
    }

    public void generateClass(ClassLoader classLoader,
                              Filer filer,
                              String className,
                              List<String> funcs) throws IOException {
        final String generatedClassName = className + "Validator";


        InputStreamReader templateFile = new InputStreamReader(
                Objects.requireNonNull(classLoader.getResourceAsStream(templatePath)));
        JavaFileObject generatedFile = filer.createSourceFile(generatedClassName);
        @Cleanup PrintWriter writer = new PrintWriter(generatedFile.openWriter());

        String code = Mustache.compiler().compile(templateFile).execute(Map.of("class", generatedClassName, "fields", funcs));
        writer.print(code);
    }


}
