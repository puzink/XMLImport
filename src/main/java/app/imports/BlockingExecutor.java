package app.imports;

import java.util.concurrent.*;

public class BlockingExecutor extends ThreadPoolExecutor {

    private final Semaphore semaphore;

    public BlockingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        semaphore = new Semaphore(workQueue.remainingCapacity());
    }

    public BlockingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        semaphore = new Semaphore(workQueue.remainingCapacity());
    }

    public BlockingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        semaphore = new Semaphore(workQueue.remainingCapacity());
    }

    public BlockingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        semaphore = new Semaphore(workQueue.remainingCapacity());
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable){

        try{
            semaphore.acquire();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        Callable<T> wrappedCall = new Callable<>() {
            @Override
            public T call() throws Exception {
                try{
                    return callable.call();
                }finally {
                    semaphore.release();
                }
            }
        };

        return super.submit(wrappedCall);
    }

}
