package ru.ifmo.rain.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Implementor implements Impler {

  /**
   * Produces code implementing class or interface specified by provided <tt>token</tt>.
   * <p>
   * Generated class classes name should be same as classes name of the type token with <tt>Impl</tt> suffix
   * added. Generated source code should be placed in the correct subdirectory of the specified
   * <tt>root</tt> directory and have correct file name. For example, the implementation of the
   * interface {@link java.util.List} should go to <tt>$root/java/util/ListImpl.java</tt>
   *
   *
   * @param token type token to create implementation for.
   * @param root root directory.
   * @throws ImplerException when implementation cannot be
   * generated.
   */
  @Override
  public void implement(Class<?> token, Path root) throws ImplerException {
    validateImplementationIsPossible(token);

    Path path = createRequiredDirectories(token, root);

    String className = token.getSimpleName() + "Impl.java";
    try (BufferedWriter writer = Files.newBufferedWriter(path.resolve(className))) {
      InterfaceImplementor implementor = new InterfaceImplementor(token);
      String implementation = implementor.implement();
      writer.write(implementation);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Path createRequiredDirectories(Class<?> token, Path root) throws ImplerException {
    Path path;
    try {
      String separator = File.separator;
      String packageName = token.getPackage().getName();
      Path dirPath = root.resolve(Paths.get(packageName.replace(".", separator)));
      path = Files.createDirectories(dirPath);
    } catch (IOException e) {
      throw new ImplerException(e);
    }
    return path;
  }

  private void validateImplementationIsPossible(Class<?> aClass) throws ImplerException {
    boolean isInterface = aClass.isInterface();

    if (!isInterface) {
      throw new ImplerException();
    }
  }

}
