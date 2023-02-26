class MyThread extends Thread {
    private Object lock;
    public MyThread(Object lock) {
        this.lock = lock;
    }
    public void run() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //线程被唤醒后执行的代码
        }
    }
}

//创建并启动线程
Object lock = new Object();
MyThread myThread = new MyThread(lock);
myThread.start();
synchronized (lock) {
    lock.notify();
}
