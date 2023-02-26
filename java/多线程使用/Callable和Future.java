class MyCallable implements Callable<Integer> {
    public Integer call() throws Exception {
        //线程执行的代码
        return 1;
    }
}

//创建并启动线程
ExecutorService executorService = Executors.newSingleThreadExecutor();
Future<Integer> future = executorService.submit(new MyCallable());
