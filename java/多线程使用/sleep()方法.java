Thread thread = new Thread(new Runnable() {
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //线程执行的代码
    }
});
thread.start();
