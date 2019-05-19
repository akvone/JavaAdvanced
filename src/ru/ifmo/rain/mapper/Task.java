package ru.ifmo.rain.mapper;

import java.util.function.Function;

public class Task<T, R> {

  private final T functionArgument;
  private R result;
  private boolean ready;
  private final Function<? super T, ? extends R> function;
  private final Object readyMonitor = new Object();

  public Task(T input, Function<? super T, ? extends R> inProcess) {
    this.functionArgument = input;
    this.function = inProcess;
  }

  public synchronized void run() {
    result = function.apply(functionArgument);
    ready = true;
    notify();
  }

  public synchronized R getResult() throws InterruptedException {
    while (!ready) {
      wait();
    }
    return result;
  }
}
