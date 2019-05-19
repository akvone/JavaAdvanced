package ru.ifmo.rain.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import ru.ifmo.rain.concurrent.IterativeParallelism;

public class ExtendedIterativeParallelism extends IterativeParallelism {

  private final ParallelMapper parallelMapper;

  public ExtendedIterativeParallelism() {
    parallelMapper = null;
  }

  public ExtendedIterativeParallelism(ParallelMapper to) {
    parallelMapper = to;
  }

  @Override
  public <T, R> List<R> runInParallel(int threadNumber, Function<List<? extends T>, R> function,
      List<? extends T> fullList) {
    if (parallelMapper == null) {
      return super.runInParallel(threadNumber, function, fullList);
    } else {
      try {
        List<List<? extends T>> partition = splitOnEachThread(threadNumber, fullList);
        return parallelMapper.map(function, partition);
      } catch (InterruptedException e) {
        e.printStackTrace();
        return null;
      }
    }

  }


  private <T> List<List<? extends T>> splitOnEachThread(int threadNumber, List<? extends T> fullList) {
    List<List<? extends T>> result = new ArrayList<>();
    int size = fullList.size();
    threadNumber = Math.min(threadNumber, size);

    for (int j = 1; j <= threadNumber; j++) {
      int k = size / threadNumber;

      int leftBound = (j - 1) * k;
      int rightBound = j != threadNumber ? j * k : size;

      var subList = fullList.subList(leftBound, rightBound);
      result.add(subList);
    }

    return result;
  }

}
