package io.vertx.up.unity;

import io.horizon.eon.VString;
import io.horizon.eon.em.typed.ChangeFlag;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2023-06-11
 */
class _Compare extends _Channel {
    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Function<T, R> fnValue, final String pojoFile) {
        return CompareT.compare(original, current, fnValue, pojoFile);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Function<T, R> fnValue) {
        return CompareT.compare(original, current, fnValue, VString.EMPTY);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Set<String> uniqueSet, final String pojoFile) {
        return CompareT.compare(original, current, uniqueSet, pojoFile);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Set<String> uniqueSet) {
        return CompareT.compare(original, current, uniqueSet, VString.EMPTY);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final Set<String> fields) {
        return CompareJ.compareJ(original, current, fields);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final String field) {
        return CompareJ.compareJ(original, current, field);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final JsonArray matrix) {
        return CompareJ.compareJ(original, current, matrix);
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final Set<String> fields) {
        return ToCommon.future(CompareJ.compareJ(original, current, fields));
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final String field) {
        return ToCommon.future(CompareJ.compareJ(original, current, field));
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final JsonArray matrix) {
        return ToCommon.future(CompareJ.compareJ(original, current, matrix));
    }

    public static <T> Future<JsonArray> compareRun(final ConcurrentMap<ChangeFlag, List<T>> compared, final Function<List<T>, Future<List<T>>> insertAsyncFn, final Function<List<T>, Future<List<T>>> updateAsyncFn) {
        return CompareT.run(compared, insertAsyncFn, updateAsyncFn);
    }
}
