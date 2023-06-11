package io.vertx.up.unity;

import io.vertx.core.*;

/**
 * @author lang : 2023-06-11
 */
class _Native extends _Ir {
    // ---------------------- Agent mode usage --------------------------

    public static Vertx nativeVertx() {
        return VertxNative.nativeVertx();
    }

    public static WorkerExecutor nativeWorker(final String name) {
        return VertxNative.nativeWorker(name, 10);
    }

    public static WorkerExecutor nativeWorker(final String name, final Integer mins) {
        return VertxNative.nativeWorker(name, mins);
    }

    public static <T> Future<T> nativeWorker(final String name, final Handler<Promise<T>> handler) {
        return VertxNative.nativeWorker(name, handler);
    }
}
