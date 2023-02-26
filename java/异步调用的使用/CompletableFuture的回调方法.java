    CompletableFuture.supplyAsync(() - > {
            // 做一些异步操作
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, World!";
        }, executor)
        .thenAcceptAsync(result - > {
            System.out.println(result);
        }, executor);

    executor.shutdown();
    }
    }
