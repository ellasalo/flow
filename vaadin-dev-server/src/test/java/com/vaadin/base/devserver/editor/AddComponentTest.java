package com.vaadin.base.devserver.editor;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class AddComponentTest extends AbstractClassBasedTest {

    @Test
    public void addBeforeFirst() throws IOException {
        setupTestClass("DemoFile");
        String source = "name = new TextField(\"Your name\")";
        String attachSource = "add(name, sayHello, sayHello2";
        String expected1 = "Button hello = new Button(\"Hello\");";
        String expected2 = "add(hello, name, sayHello, sayHello2";

        testAddBeforeComponent(source, attachSource, expected1, expected2,
                ComponentType.BUTTON, "Hello");
    }

    @Test
    public void addAfterFirst() throws IOException {
        setupTestClass("DemoFile");
        String source = "name = new TextField(\"Your name\")";
        String attachSource = "add(name, sayHello, sayHello2";

        String expected1 = "Button hello = new Button(\"Hello\");";
        String expected2 = "add(name, hello, sayHello, sayHello2";
        testAddAfterComponent(source, attachSource, expected1, expected2,
                ComponentType.BUTTON, "Hello");
    }

    @Test
    public void addAfterLast() throws IOException {
        setupTestClass("DemoFile");
        String source = "Button sayHello4 = new Button()";
        String attachSource = "add(name, sayHello, sayHello2, sayHello3, sayHello4);";
        String expected1 = "Button hello = new Button(\"Hello\");";
        String expected2 = "add(name, sayHello, sayHello2, sayHello3, sayHello4, hello);";
        testAddAfterComponent(source, attachSource, expected1, expected2,
                ComponentType.BUTTON, "Hello");
    }

    @Test
    public void addBeforeLast() throws IOException {
        setupTestClass("DemoFile");
        String source = "Button sayHello4 = new Button()";
        String attachSource = "add(name, sayHello, sayHello2, sayHello3, sayHello4);";
        String expected1 = "Button hello = new Button(\"Hello\");";
        String expected2 = "add(name, sayHello, sayHello2, sayHello3, hello, sayHello4);";
        testAddBeforeComponent(source, attachSource, expected1, expected2,
                ComponentType.BUTTON, "Hello");
    }

    @Test
    public void addAfterInlineComponent() throws IOException {
        setupTestClass("DemoFile");
        String source = "new Button(\"Say hello6\")";
        String attachSource = "add(sayHello5, new Button(\"Say hello6\"));";

        String expected1 = "hello = new Button(\"Hello\");";
        String expected2 = "add(sayHello5, new Button(\"Say hello6\"), hello);";
        testAddAfterComponent(source, attachSource, expected1, expected2,
                ComponentType.BUTTON, "Hello");
    }

    @Test
    public void addBeforeInlineComponent() throws IOException {
        setupTestClass("DemoFile");
        String source = "new Button(\"Say hello6\")";
        String attachSource = "add(sayHello5, new Button(\"Say hello6\"));";
        String expected1 = "Button hello = new Button(\"Hello\");";
        String expected2 = "add(sayHello5, hello, new Button(\"Say hello6\"));";
        testAddBeforeComponent(source, attachSource, expected1, expected2,
                ComponentType.BUTTON, "Hello");
    }

    @Test
    public void addToEmptyView() throws Exception {
        setupTestClass("EmptyView");
        // Create is the class declaration when there is no constructor
        int create = getLineNumber(testFile, "public class EmptyView");
        int attach = create;
        editor.addComponentInside(testFile, create, attach,
                ComponentType.BUTTON, "Hello world");

        assertTestFileContains(
                "Button helloWorld = new Button(\"Hello world\");");
        assertTestFileContains("add(helloWorld)");
    }

    @Test
    public void addToEmptyViewWithConstructor() throws Exception {
        setupTestClass("EmptyViewWithConstructor");
        // Create is the constructor
        int create = getLineNumber(testFile, "public EmptyView() {");
        int attach = create;
        editor.addComponentInside(testFile, create, attach,
                ComponentType.BUTTON, "Hello world");

        assertTestFileContains(
                "Button helloWorld = new Button(\"Hello world\");");
        assertTestFileContains("add(helloWorld)");
    }

    @Test
    public void addTwiceToEmptyView() throws Exception {
        setupTestClass("EmptyView");
        // Create is the class declaration when there is no constructor
        int create = getLineNumber(testFile, "public class EmptyView");
        int attach = create;
        editor.addComponentInside(testFile, create, attach,
                ComponentType.BUTTON, "Hello world");
        int helloWorldCreate = getLineNumber(testFile, "new Button");
        int helloWorldAttach = getLineNumber(testFile, "add(helloWorld)");

        editor.addComponentAfter(testFile, helloWorldCreate, helloWorldAttach,
                ComponentType.TEXTFIELD, "Goodbye world");

        System.out.println(getTestFileContents());
        assertTestFileContains(
                "Button helloWorld = new Button(\"Hello world\");");
        assertTestFileContains(
                "TextField goodbyeWorld = new TextField(\"Goodbye world\");");
        assertTestFileContains("add(helloWorld, goodbyeWorld)");
    }

    private void testAddAfterComponent(String source, String attachSource,
            String expected1, String expected2, ComponentType componentType,
            String... constructorArguments) throws IOException {
        String constructorString = "new "
                + getSimpleName(componentType.getClassName()) + "("
                + Stream.of(constructorArguments).map(arg -> "\"" + arg + "\"")
                        .collect(Collectors.joining(", "));
        assertTestFileNotContains(constructorString);
        assertTestFileNotContains(expected1);
        assertTestFileNotContains(expected2);

        int instantiationLineNumber = getLineNumber(testFile, source);
        int attachLineNumber = getLineNumber(testFile, attachSource);
        editor.addComponentAfter(testFile, instantiationLineNumber,
                attachLineNumber, componentType, constructorArguments);
        assertTestFileContains(constructorString);
        assertTestFileContains(expected1);
        assertTestFileContains(expected2);
    }

    private void testAddBeforeComponent(String source, String attachSource,
            String expected1, String expected2, ComponentType componentType,
            String... constructorArguments) throws IOException {
        String constructorString = "new "
                + getSimpleName(componentType.getClassName()) + "("
                + Stream.of(constructorArguments).map(arg -> "\"" + arg + "\"")
                        .collect(Collectors.joining(", "));
        assertTestFileNotContains(constructorString);
        assertTestFileNotContains(expected1);
        assertTestFileNotContains(expected2);

        int instantiationLineNumber = getLineNumber(testFile, source);
        int attachLineNumber = getLineNumber(testFile, attachSource);
        editor.addComponentBefore(testFile, instantiationLineNumber,
                attachLineNumber, componentType, constructorArguments);
        assertTestFileContains(constructorString);
        assertTestFileContains(expected1);
        assertTestFileContains(expected2);
    }

    private String getSimpleName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

}
