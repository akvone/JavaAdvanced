package ru.ifmo.rain.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.Tester;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Accessible;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.Descriptor;
import info.kgeorgiy.java.advanced.implementor.basic.interfaces.standard.RandomAccess;
import info.kgeorgiy.java.advanced.implementor.full.interfaces.standard.CachedRowSet;
import java.nio.file.Path;
import java.util.SortedSet;

public class Main {

  public static void main(String[] args) throws ImplerException {
    Tester.main("interface", Implementor.class.getName());
//    new Implementor().implement(CachedRowSet.class, Path.of("test03_standardInterfaces"));
  }
}
