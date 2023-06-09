package io.vertx.zero.exception;

import io.horizon.exception.BootingException;

import java.lang.reflect.Method;

public class IpcMethodArgException extends BootingException {

    public IpcMethodArgException(final Class<?> clazz,
                                 final Method method) {
        super(clazz, method);
    }

    @Override
    public int getCode() {
        return -40046;
    }
}
