package com.personal;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileParser {
    public static void main(String[] args) throws IOException {
        JavaSymbolSolver solver = new JavaSymbolSolver(new CombinedTypeSolver());
        StaticJavaParser.getConfiguration().setSymbolResolver(solver);
        CompilationUnit compilationUnit = StaticJavaParser.parse(new File("./src/main/java/com/personal/Person.java"));
        ClassOrInterfaceDeclaration sourceFile = compilationUnit.getTypes().get(0).asClassOrInterfaceDeclaration();

        /*Task 1
        * For each given method find
        * 1. name of the method
        * 2. return type
        * 3. start line
        * 4. end line
        * 5. parameter type (if applicable)
        * 6. parameter name (if applicable)
        * */
        AsciiTable task1 = new AsciiTable();
        task1.addRule();
        task1.addRow(null, null, null, null, null, "Task 1: Methods list and their details").setTextAlignment(TextAlignment.CENTER);
        task1.addRule();
        task1.addRow("", "Name", "Return Type", "Start Line", "End Line", "Parameters");
        task1.addRule();
        List<MethodDeclaration> methods = sourceFile.getMethods();
        for (int i = 1; i <= methods.size(); i++) {
            MethodDeclaration method = methods.get(i-1);
            int startLine = -1;
            int endLine = -1;
            if(method.getBegin().isPresent()){
                startLine = method.getBegin().get().line;
            }

            if(method.getEnd().isPresent()){
                endLine = method.getEnd().get().line;
            }
            StringBuilder builder = new StringBuilder();
            for (Parameter parameter : method.getParameters()) {
                builder.append("<br>").append("Name: ").append(parameter.getName()).append(" Type: ").append(parameter.getType());
            }
            task1.addRow("Method " + i, method.getName(), method.getType(), startLine != -1 ? startLine : "-", endLine != -1 ? endLine : "-", builder.toString());
            task1.addRule();
        }
        System.out.println(task1.render());
    }
}
