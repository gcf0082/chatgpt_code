class Counter {
    private int count = 0;
    public synchronized void increment() {
        count++;
    }
}

//使用同步锁的线程
Counter counter = new Counter();
Thread thread1 = new Thread(new Runnable() {
    public void run() {
        for (int i = 0; i < 100000; i++) {
            counter.increment();
        }
    }
});
Thread thread2 = new Thread(new Runnable() {
    public void run() {
        for (int i = 0; i < 100000; i++) {
            counter.increment();
        }
    }
});
thread1.start();
thread2.start();
thread1.join();
thread2.join();
System.out.println(counter.getCount());
