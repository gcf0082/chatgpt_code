ExecutorService executorService = Executors.newFixedThreadPool(2);
for (int i = 0; i < 5; i++) {
    executorService.execute(new Runnable() {
        public void run() {
            //线程执行的代码
        }
    });
}
executorService.shutdown();
