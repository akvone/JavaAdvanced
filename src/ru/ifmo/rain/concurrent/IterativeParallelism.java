package ru.ifmo.rain.concurrent;

import static java.util.stream.Collectors.toList;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IterativeParallelism implements ListIP {

  /**
   * Splits fullList into equal subLists.
   * @return mapped values according to function
   */
  private <T, R> List<R> runInParallel(int threadNumber, Function<List<? extends T>, R> function,
      List<? extends T> fullList) {
    List<RunnableWithResult<T, R>> customRunnableList = new ArrayList<>();
    int size = fullList.size();

    if (threadNumber > size) {
      return Collections.singletonList(function.apply(fullList));
    }

    for (int j = 1; j <= threadNumber; j++) {
      int k = size / threadNumber;

      int leftBound = (j - 1) * k;
      int rightBound = j != threadNumber ? j * k : size;

      var subList = fullList.subList(leftBound, rightBound);
      var rwr = new RunnableWithResult<>(function, subList);
      customRunnableList.add(rwr);
    }

    runAllUntilEnd(customRunnableList);

    return customRunnableList.stream().map(RunnableWithResult::getResult).collect(toList());
  }

  private <T, R> void runAllUntilEnd(List<RunnableWithResult<T, R>> runnableList) {
    List<Thread> threadList = runnableList.stream().map(Thread::new).collect(toList());
    threadList.forEach(Thread::start);
    threadList.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }


  // Simple
  @Override
  public <T> T maximum(int threadNumber, List<? extends T> fullList, Comparator<? super T> comparator) {
    Function<List<? extends T>, T> function = (subList) -> subList.stream().max(comparator).get();

    return function.apply(runInParallel(threadNumber, function, fullList));
  }

  @Override
  public <T> T minimum(int threadNumber, List<? extends T> fullList, Comparator<? super T> comparator) {
    Function<List<? extends T>, T> function = (subList) -> subList.stream().min(comparator).get();

    return function.apply(runInParallel(threadNumber, function, fullList));
  }

  @Override
  public <T> boolean all(int threadNumber, List<? extends T> fullList, Predicate<? super T> predicate) {
    Function<List<? extends T>, Boolean> function = (subList) -> subList.stream().allMatch(predicate);

    return runInParallel(threadNumber, function, fullList).stream().allMatch((e) -> e);
  }

  @Override
  public <T> boolean any(int threadNumber, List<? extends T> fullList, Predicate<? super T> predicate) {
    Function<List<? extends T>, Boolean> function = (subList) -> subList.stream().anyMatch(predicate);

    return runInParallel(threadNumber, function, fullList).stream().anyMatch((e) -> e);
  }


  // Difficult
  @Override
  public String join(int threadNumber, List<?> fullList) {
    Function<List<?>, String> function = (subList) -> subList.stream().map(Object::toString).collect(Collectors.joining());

    List<String> strings = runInParallel(threadNumber, function, fullList);
    return String.join("", strings);
  }

  @Override
  public <T> List<T> filter(int threadNumber, List<? extends T> fullList, Predicate<? super T> predicate) {
    Function<List<? extends T>, List<T>> function = (subList) -> subList.stream().filter(predicate).collect(toList());

    return runInParallel(threadNumber, function, fullList)
        .stream()
        .flatMap(Collection::stream)
        .collect(toList());
  }

  @Override
  public <T, U> List<U> map(int threadNumber, List<? extends T> fullList, Function<? super T, ? extends U> mapFunction) {
    Function<List<? extends T>, List<? super T>> function = (subList) -> subList.stream().map(mapFunction).collect(toList());

    return (List<U>) runInParallel(threadNumber, function, fullList)
        .stream()
        .flatMap(Collection::stream)
        .collect(toList());
  }
}
