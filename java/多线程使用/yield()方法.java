Thread thread1 = new Thread(new Runnable() {
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("Thread 1: " + i);
            Thread.yield();
       
