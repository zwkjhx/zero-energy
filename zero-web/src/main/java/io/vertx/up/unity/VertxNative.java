package io.vertx.up.unity;

import io.vertx.core.*;
import io.vertx.up.ZeroLauncher;

import java.util.concurrent.TimeUnit;

class VertxNative {

    static Vertx nativeVertx() {
        return ZeroLauncher.nativeRef();
    }

    static WorkerExecutor nativeWorker(final String name, final Integer minutes) {
        return nativeVertx().createSharedWorkerExecutor(name, 2, minutes, TimeUnit.MINUTES);
    }

    static <T> Future<T> nativeWorker(final String name, final Handler<Promise<T>> handler) {
        final Promise<T> promise = Promise.promise();
        final WorkerExecutor executor = nativeWorker(name, 10);
        executor.executeBlocking(
            handler,
            post -> {
                // Fix Issue:
                // Task io.vertx.core.impl.TaskQueue$$Lambda$367/0x00000008004f3440@2b1e3784 rejected from
                // java.util.concurrent.ThreadPoolExecutor@1db1d316
                // [Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0]
                executor.close();
                promise.complete(post.result());
            }
        );
        return promise.future();
    }
}
