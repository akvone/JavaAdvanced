package ru.ifmo.rain.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;


public class ParallelMapperImpl implements ParallelMapper {

  private final List<Thread> threadPool = new ArrayList<>();
  private final LinkedBlockingQueue<Task<?, ?>> tasksQueue = new LinkedBlockingQueue<>();


  public ParallelMapperImpl(int threads) {
    Runnable r = () -> {
      try {
        while (!Thread.interrupted()) {
          tasksQueue.take().run();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    };
    for (int i = 0; i < threads; i++) {
      Thread t = new Thread(r);
      threadPool.add(t);
      t.start();
    }
  }

  @Override
  public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
    List<Task<? super T, ? extends R>> tasks = runTasks(f, args);

    return getResult(tasks);
  }

  private <T, R> List<Task<? super T, ? extends R>> runTasks(Function<? super T, ? extends R> f,
      List<? extends T> args) throws InterruptedException {
    List<Task<? super T, ? extends R>> tasks = new ArrayList<>();
    for (T arg : args) {
      Task<? super T, ? extends R> task = new Task<>(arg, f);
      tasks.add(task);
      tasksQueue.put(task);
    }

    return tasks;
  }

  private <T, R> List<R> getResult(List<Task<? super T, ? extends R>> tasks) throws InterruptedException {
    List<R> result = new ArrayList<>();
    for (Task<? super T, ? extends R> task : tasks) {
      result.add(task.getResult());
    }

    return result;
  }

  @Override
  public void close() {
    threadPool.forEach(Thread::interrupt);
  }
}
