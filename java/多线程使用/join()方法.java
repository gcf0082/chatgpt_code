Thread thread1 = new Thread(new Runnable() {
    public void run() {
        //线程执行的代码
    }
});
Thread thread2 = new Thread(new Runnable() {
    public void run() {
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //线程执行的代码
    }
});
thread1.start();
thread2.start();
