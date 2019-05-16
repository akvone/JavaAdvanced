package ru.ifmo.rain.student;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class StudentQueryImpl implements StudentGroupQuery {

  @Override
  public List<String> getFirstNames(List<Student> list) {
    return map(list, Student::getFirstName);
  }

  @Override
  public List<String> getLastNames(List<Student> list) {
    return map(list, Student::getLastName);
  }

  @Override
  public List<String> getGroups(List<Student> list) {
    return map(list, Student::getGroup);
  }

  @Override
  public List<String> getFullNames(List<Student> list) {
    return map(list, s -> format("%s %s", s.getFirstName(), s.getLastName()));
  }

  private List<String> map(Collection<Student> students, Function<Student, String> mapFunction) {
    return students.stream().map(mapFunction).collect(toList());
  }

  @Override
  public SortedSet<String> getDistinctFirstNames(List<Student> list) {
    return new TreeSet<>(getFirstNames(list));
  }

  @Override
  public String getMinStudentFirstName(List<Student> list) {
    return list.stream()
        .min(comparingInt(Student::getId))
        .map(Student::getFirstName)
        .orElse("");
  }

  @Override
  public List<Student> sortStudentsById(Collection<Student> collection) {
    return collection.stream().sorted(comparingInt(Student::getId)).collect(toList());
  }

  @Override
  public List<Student> sortStudentsByName(Collection<Student> collection) {
    return collection.stream()
        .sorted(comparing(Student::getLastName).thenComparing(Student::getFirstName).thenComparingInt(Student::getId))
        .collect(toList());
  }

  @Override
  public List<Student> findStudentsByFirstName(Collection<Student> collection, String s) {
    return sortStudentsByName(filter(collection, st -> st.getFirstName().equals(s)));
  }

  @Override
  public List<Student> findStudentsByLastName(Collection<Student> collection, String s) {
    return sortStudentsByName(filter(collection, st -> st.getLastName().equals(s)));
  }

  @Override
  public List<Student> findStudentsByGroup(Collection<Student> collection, String s) {
    return sortStudentsByName(filter(collection, st -> st.getGroup().equals(s)));
  }

  private List<Student> filter(Collection<Student> collection, Predicate<Student> predicate) {
    return collection.stream().filter(predicate).collect(toList());
  }

  @Override
  public Map<String, String> findStudentNamesByGroup(Collection<Student> collection, String s) {
    return collection.stream()
        .filter(st -> st.getGroup().equals(s))
        .collect(toMap(Student::getLastName, Student::getFirstName, (s1, s2) -> s1.compareTo(s2) > 0 ? s2 : s1));
  }

  // Difficult

  @Override
  public List<Group> getGroupsByName(Collection<Student> collection) {
    return group(collection).entrySet().stream()
        .map(e -> new Group(e.getKey(), sortStudentsByName(e.getValue())))
        .sorted(comparing(Group::getName))
        .collect(toList());
  }

  @Override
  public List<Group> getGroupsById(Collection<Student> collection) {
    return group(collection).entrySet().stream()
        .map(e -> new Group(e.getKey(), sortStudentsById(e.getValue())))
        .sorted(comparing(Group::getName))
        .collect(toList());
  }

  @Override
  public String getLargestGroup(Collection<Student> collection) {
    return group(collection).entrySet().stream()
        .max(createComplexComparator1())
        .map(Entry::getKey)
        .orElse("");
  }

  private Comparator<Entry<String, List<Student>>> createComplexComparator1() {
    return Comparator.<Entry<String, List<Student>>>comparingInt(e -> e.getValue().size())
        .thenComparing(Comparator.<Entry<String, List<Student>>, String>comparing(Entry::getKey).reversed());
  }

  @Override
  public String getLargestGroupFirstName(Collection<Student> collection) {
    return group(collection).entrySet().stream()
        .collect(toMap(Entry::getKey, e -> ((int) e.getValue().stream().map(Student::getFirstName).distinct().count())))
        .entrySet().stream()
        .max(createComplexComparator2())
        .map(Entry::getKey)
        .orElse("");
  }

  private Comparator<Entry<String, Integer>> createComplexComparator2() {
    return Comparator.<Entry<String, Integer>>comparingInt(Entry::getValue)
        .thenComparing(Comparator.<Entry<String, Integer>, String>comparing(Entry::getKey).reversed());
  }

  private Map<String, List<Student>> group(Collection<Student> collection) {
    return collection.stream().collect(groupingBy(Student::getGroup));
  }
}
