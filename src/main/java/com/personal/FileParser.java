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
import org.apache.commons.lang3.ArrayUtils;
//import sun.security.util.Arra;

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
        CompilationUnit compilationUnit = StaticJavaParser.parse(new File("./src/main/java/com/personal/Example.java"));
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
        AsciiTable task1 = new AsciiTable();
        task1.addRule();
        task1.addRow(null, null, null, null, null, "Task 1: Methods list and their details").setTextAlignment(TextAlignment.CENTER);
        task1.addRule();
        task1.addRow("", "Name", "Return Type", "Start Line", "End Line", "Parameters");
        task1.addRule();
        for (int i = 1; i <= methods.size(); i++) {
            MethodDeclaration method = methods.get(i - 1);
            int startLine = -1;
            int endLine = -1;
            if (method.getBegin().isPresent()) {
                startLine = method.getBegin().get().line;
            }

            if (method.getEnd().isPresent()) {
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



        /*Task 2
         * For each line in a method body, if it is a function call
         * find other methods that have been called on the variable
         */
        System.out.println("-------------Task:2-------------");
        HashMap<String, ArrayList<String>> methodCalls = new HashMap<>();
        methods.forEach(method -> {
            method.getBody().ifPresent(body -> {
                for (Statement statement : body.getStatements()) {
                    statement.ifExpressionStmt(expressionStmt -> {
                        expressionStmt.getExpression().ifVariableDeclarationExpr(variableDeclarationExpr -> {
                            String nameAsString = variableDeclarationExpr.getVariables().get(0).getNameAsString();
                            if (methodCalls.containsKey(nameAsString)) {
                                methodCalls.get(nameAsString).add(variableDeclarationExpr.getVariables().get(0).getType().asString());
                            } else {
                                ArrayList<String> objects = new ArrayList<>();
                                objects.add(variableDeclarationExpr.getVariables().get(0).getType().asString());
                                methodCalls.put(nameAsString, objects);
                            }
                            Object[] array = methodCalls.get(nameAsString).toArray();
                            ArrayUtils.reverse(array);
                            System.out.println("Found method call for : " + nameAsString + ", List until now: " + Arrays.toString(array));
                            methodCalls.forEach((key, value) -> {
                                //System.out.println(key);
                                System.out.println(Arrays.toString(value.toArray()));
                            });

                        });

                        expressionStmt.getExpression().ifMethodCallExpr(methodCallExpr -> {
                            methodCallExpr.getScope().ifPresent(expression -> {
                                resolveMethodCalls(methodCallExpr, methodCalls);

                            });
                        });
                    });
                }
            });
        });


/*
         * Task 3: For each receiver variable collect all the methods that take this variable as a parameter.
         */

        methods.forEach(method -> {
            method.getBody().ifPresent(body -> {
                for (Statement statement : body.getStatements()) {
                    statement.ifExpressionStmt(expressionStmt -> {
                        expressionStmt.getExpression().ifVariableDeclarationExpr(variableDeclarationExpr -> {
                            variableDeclarationExpr.getVariables().get(0).getInitializer().ifPresent(expression -> {
                                expression.ifObjectCreationExpr(objectCreationExpr -> {
                                    objectCreationExpr.getArguments().forEach(argument -> {
                                        if(methodCalls.containsKey(argument.toString())){
                                            int line = objectCreationExpr.getBegin().get().line;
                                            System.out.println("-------------Task:3-------------");
                                            System.out.println(argument+" was used as parameter while creating object for "+objectCreationExpr.getType()+" on line: "+line);
                                        }
                                    });
                                });
                            });

                        });
                        expressionStmt.getExpression().ifMethodCallExpr(methodCallExpr -> {
                            methodCallExpr.getArguments().forEach(argument -> {
                                if(methodCalls.containsKey(argument.toString())){
                                    int line = methodCallExpr.getBegin().get().line;
                                   System.out.println(argument+" was used as a parameter in method call "+methodCallExpr.getNameAsString()+" on line: "+line);
                                }
                            });
                        });
                    });
                }
            });
        });
    }

    private static void resolveMethodCalls(MethodCallExpr methodCallExpr, HashMap<String, ArrayList<String>> methodCalls) {
        Expression expression = null;
        if (methodCallExpr.getScope().isPresent()) {
            expression = methodCallExpr.getScope().get();
        }

        if (expression != null) {
            expression.ifNameExpr(nameExpr -> {
                if (methodCalls.containsKey(nameExpr.getNameAsString())) {
                    methodCalls.get(nameExpr.getNameAsString()).add(methodCallExpr.getNameAsString());
                } else {
                    ArrayList<String> objects = new ArrayList<>();
                    objects.add(methodCallExpr.getNameAsString());
                    methodCalls.put(nameExpr.getNameAsString(), objects);
                }
                Object[] array = methodCalls.get(nameExpr.getNameAsString()).toArray();
                ArrayUtils.reverse(array);
                System.out.println("Found method call for : " + nameExpr.getNameAsString() + ", List until now: " + Arrays.toString((array)));

            });

            if (expression.isMethodCallExpr()) {
                resolveMethodCalls(expression.asMethodCallExpr(), methodCalls);
            }
        }
    }
}
