package io.vertx.up.unity;

import io.horizon.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.commune.Envelop;
import io.vertx.up.eon.KName;
import io.vertx.up.util.Ut;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * @author lang : 2023-06-11
 */
class _GetFrom extends _Get {

    public static <T> T fromJson(final JsonObject data, final Class<T> clazz) {
        return InputFrom.fromJson(data, clazz, "");
    }

    public static <T> List<T> fromJson(final JsonArray array, final Class<T> clazz) {
        return InputFrom.fromJson(array, clazz, "");
    }

    public static <T> List<T> fromPage(final JsonObject data, final Class<T> clazz) {
        final JsonArray pageData = Ut.valueJArray(data.getJsonArray(KName.LIST));
        return fromJson(pageData, clazz);
    }

    public static <T> T fromJson(final JsonObject data, final Class<T> clazz, final String pojo) {
        return InputFrom.fromJson(data, clazz, pojo);
    }

    public static <T> List<T> fromJson(final JsonArray array, final Class<T> clazz, final String pojo) {
        return InputFrom.fromJson(array, clazz, pojo);
    }

    /*
     * Envelop building here
     * 1) envelop: ( Get different Envelop )
     * 2) future: ( Wrapper Future.successedFuture / Future.failureFuture ) at same time
     * 3) handler: ( Handler<AsyncResult<T>> )
     * 4) compare: ( Compare two object )
     * 5) complex:
     *    - JsonObject -> condition -> executor
     *    - JsonArray -> condition -> grouper -> executor
     */
    public static Envelop fromEnvelop(final Class<? extends WebException> clazz, final Object... args) {
        return ToCommon.toEnvelop(clazz, args);
    }

    public static <T> Envelop fromEnvelop(final T entity) {
        return ToCommon.toEnvelop(entity);
    }

    public static <T> Envelop fromEnvelop(final T entity, final WebException error) {
        return ToCommon.toEnvelop(entity, error);
    }

    public static <T> Future<T> fromAsync(final CompletionStage<T> state) {
        return Async.fromAsync(state);
    }

    public static <T> T fromEnvelop(final Envelop envelop, final Class<T> clazz, final String pojo) {
        return InputFrom.fromJson(getJson(envelop), clazz, pojo);
    }

    public static JsonObject fromEnvelop(final Envelop envelop, final String pojo) {
        return InputFrom.fromJson(getJson(envelop), pojo);
    }

    public static <T> T fromEnvelop1(final Envelop envelop, final Class<T> clazz, final String pojo) {
        return InputFrom.fromJson(getJson1(envelop), clazz, pojo);
    }

    public static JsonObject fromEnvelop1(final Envelop envelop, final String pojo) {
        return InputFrom.fromJson(getJson1(envelop), pojo);
    }

    public static JsonObject fromEnvelop2(final Envelop envelop, final String pojo) {
        return InputFrom.fromJson(getJson2(envelop), pojo);
    }

    public static <T> T fromEnvelop2(final Envelop envelop, final Class<T> clazz, final String pojo) {
        return InputFrom.fromJson(getJson2(envelop), clazz, pojo);
    }
}
