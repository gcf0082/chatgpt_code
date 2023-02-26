class MyRunnable implements Runnable {
    public void run() {
        //线程执行的代码
    }
}

//创建并启动线程
Thread thread = new Thread(new MyRunnable());
thread.start();
