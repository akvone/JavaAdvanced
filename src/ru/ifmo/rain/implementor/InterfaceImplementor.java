package ru.ifmo.rain.implementor;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

class InterfaceImplementor {

  private static final String LINE_SEPARATOR = System.lineSeparator();
  private static final String LONG_LINE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;

  private final Set<Class> classesToImport = new HashSet<>();
  private final StringBuilder classDefinitionBuilder = new StringBuilder();

  private final Class clazz;

  InterfaceImplementor(Class clazz) {
    this.clazz = clazz;
  }

  String implement() {
    StringBuilder result = new StringBuilder();

    String classDefinition = analyzeAndPrepareClassDefinition(); // Must go first
    String packageDefinition = preparePackage();
    String importsDefinition = prepareImports();

    result.append(packageDefinition);
    result.append(LONG_LINE_SEPARATOR);
    result.append(importsDefinition);
    result.append(LONG_LINE_SEPARATOR);
    result.append(classDefinition);

    return result.toString();
  }

  private String preparePackage() {
    if (!clazz.getPackage().getName().equals("")) {
      return format("package %s;", clazz.getPackage().getName());
    }

    return "";
  }

  private String prepareImports() {
    StringBuilder result = new StringBuilder();
    for (Class classToImport : classesToImport) {
      result.append(format("import %s;", classToImport.getTypeName()));
      result.append("\n");
    }

    return result.toString();
  }

  private String analyzeAndPrepareClassDefinition() {
    String className = clazz.getSimpleName() + "Impl";
//      String interfaceName = getSimpleNameWithImportRegistration(clazz);
    String interfaceName = clazz.getCanonicalName();

    append(format("public class %s implements %s {", className, interfaceName));

    for (Method method : getAbstractMethods(clazz)) {
      append(LONG_LINE_SEPARATOR);
      appendMethod(method);
    }

    append(LINE_SEPARATOR);
    append("}");

    return classDefinitionBuilder.toString();
  }

  private List<Method> getAbstractMethods(Class aClass) {
    Map<String, Method> map = new HashMap<>();
    findMethodsRecursively(map, aClass);

    return new ArrayList<>(map.values());
  }

  private void findMethodsRecursively(Map<String, Method> map, Class aClass) {
    for (Method method : aClass.getDeclaredMethods()) {
      String checkString = method.getName() + Arrays.toString(method.getParameterTypes());
      map.putIfAbsent(checkString, method);
    }
    if (aClass.getSuperclass() != null) {
      findMethodsRecursively(map, aClass.getSuperclass());
    }
    for (Class anInterface : aClass.getInterfaces()) {
      findMethodsRecursively(map, anInterface);
    }
  }

  private void appendMethod(Method method) {
    Class<?> returnType = method.getReturnType();
    String modifiers = prepareModifiers(method);
//      String returnTypeName = getSimpleNameWithImportRegistration(returnType);
    String returnTypeName = returnType.getCanonicalName();
    String methodName = method.getName();
    String parameters = prepareParameters(method.getParameterTypes());

    String template = "\t%s %s %s(%s){"; // public void someMethod(Object arg1){
    append(format(template, modifiers, returnTypeName, methodName, parameters));
    append(LINE_SEPARATOR);

    if (!returnType.equals(void.class)) {
      String defaultValue = getDefaultValue(returnType);
      String returnTemplate = "\t\treturn %s;"; // return null;
      append(format(returnTemplate, defaultValue));
    }
    append(LINE_SEPARATOR);
    append("\t}");
  }

  private String prepareModifiers(Method method) {
    return Modifier.toString(~Modifier.ABSTRACT & method.getModifiers() & Modifier.methodModifiers());
  }

  private String getDefaultValue(Class<?> returnType) {
//      return String.valueOf(Array.get(Array.newInstance(returnType, 1), 0));
    if (returnType.getSimpleName().equals("boolean")) {
      return "false";
    } else if (returnType.isPrimitive()) {
      return "0";
    } else {
      return "null";
    }
  }

  private String prepareParameters(Class[] params) {
    List<String> paramDefinitions = new ArrayList<>();
    for (int i = 0; i < params.length; i++) {
      String paramDefinition = params[i].getCanonicalName() + " " + "arg" + (i + 1);
      paramDefinitions.add(paramDefinition);
    }

    return String.join(", ", paramDefinitions);
  }

  private String getSimpleNameWithImportRegistration(Class aClass) {
    Class type = aClass.isArray() ? aClass.getComponentType() : aClass;

    if (type.isPrimitive()) {
      return aClass.getSimpleName();
    } else {
      Predicate<Class> p = Predicate
          .not((Class c) -> c.getTypeName().equals(type.getTypeName()))
          .and((Class c) -> c.getSimpleName().equals(type.getSimpleName()));

      boolean clashes = classesToImport.stream().anyMatch(p);

      if (!clashes) {
        classesToImport.add(type);
        return aClass.getSimpleName();
      } else {
        return type.getTypeName();
      }
    }
  }

  private void append(String... strings) {
    for (String string : strings) {
      classDefinitionBuilder.append(string);
    }
  }
}
