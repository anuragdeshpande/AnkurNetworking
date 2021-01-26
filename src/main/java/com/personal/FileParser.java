package com.personal;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileParser {
    public static void main(String[] args) throws IOException {
        JavaSymbolSolver solver = new JavaSymbolSolver(new CombinedTypeSolver());
        StaticJavaParser.getConfiguration().setSymbolResolver(solver);
        CompilationUnit compilationUnit = StaticJavaParser.parse(new File("./src/main/java/com/personal/Person.java"));
        ClassOrInterfaceDeclaration sourceFile = compilationUnit.getTypes().get(0).asClassOrInterfaceDeclaration();
        List<MethodDeclaration> methods = sourceFile.getMethods();
        /*Task 1
        * For each given method find
        * 1. name of the method
        * 2. return type
        * 3. start line
        * 4. end line
        * 5. parameter type (if applicable)
        * 6. parameter name (if applicable)
        * */
//        AsciiTable task1 = new AsciiTable();
//        task1.addRule();
//        task1.addRow(null, null, null, null, null, "Task 1: Methods list and their details").setTextAlignment(TextAlignment.CENTER);
//        task1.addRule();
//        task1.addRow("", "Name", "Return Type", "Start Line", "End Line", "Parameters");
//        task1.addRule();
//        for (int i = 1; i <= methods.size(); i++) {
//            MethodDeclaration method = methods.get(i-1);
//            int startLine = -1;
//            int endLine = -1;
//            if(method.getBegin().isPresent()){
//                startLine = method.getBegin().get().line;
//            }
//
//            if(method.getEnd().isPresent()){
//                endLine = method.getEnd().get().line;
//            }
//            StringBuilder builder = new StringBuilder();
//            for (Parameter parameter : method.getParameters()) {
//                builder.append("<br>").append("Name: ").append(parameter.getName()).append(" Type: ").append(parameter.getType());
//            }
//            task1.addRow("Method " + i, method.getName(), method.getType(), startLine != -1 ? startLine : "-", endLine != -1 ? endLine : "-", builder.toString());
//            task1.addRule();
//        }
//        System.out.println(task1.render());



        /*Task 2
        * For each line in a method body, if it is a function call
        * find other methods that have been called on the variable
        */
        HashMap<String, ArrayList<Expression>> methodCalls = new HashMap<>();
        methods.forEach(method -> {
            method.getBody().ifPresent(body -> {
                for (Statement statement : body.getStatements()) {
                    statement.ifExpressionStmt(expressionStmt -> {
                        expressionStmt.getExpression().ifVariableDeclarationExpr(variableDeclarationExpr -> {
                            String nameAsString = variableDeclarationExpr.getVariables().get(0).getNameAsString();
                            if(methodCalls.containsKey(nameAsString)){
                                methodCalls.get(nameAsString).add(variableDeclarationExpr);
                            } else {
                                ArrayList<Expression> objects = new ArrayList<>();
                                objects.add(variableDeclarationExpr);
                                methodCalls.put(nameAsString, objects);
                            }
                        });

                        expressionStmt.getExpression().ifMethodCallExpr(methodCallExpr -> {
                            methodCallExpr.getScope().ifPresent(expression -> {
                                expression.ifNameExpr(nameExpr -> {
                                    if(methodCalls.containsKey(nameExpr.getNameAsString())){
                                        methodCalls.get(nameExpr.getNameAsString()).add(methodCallExpr);
                                    } else {
                                        ArrayList<Expression> objects = new ArrayList<>();
                                        methodCalls.put(nameExpr.getNameAsString(), objects);
                                    }
                                });
                            });
                        });
                    });
                }
            });
        });
        methodCalls.forEach((key, value) -> {
            System.out.println(key);
            System.out.println(Arrays.toString(value.toArray()));
        });



    }
}
