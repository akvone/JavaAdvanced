package ru.ifmo.rain.concurrent;

import java.util.List;
import java.util.function.Function;

class RunnableWithResult<T, R> implements Runnable {

  private final Function<List<? extends T>, R> function;
  private final List<? extends T> list;
  private R result;

  public RunnableWithResult(Function<List<? extends T>, R> function, List<? extends T> list) {
    this.function = function;
    this.list = list;
  }

  @Override
  public void run() {
    result = function.apply(list);
  }

  public R getResult() {
    return result;
  }
}
