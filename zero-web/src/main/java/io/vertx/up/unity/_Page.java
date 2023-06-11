package io.vertx.up.unity;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.eon.KName;
import io.vertx.up.util.Ut;

import java.util.function.Function;

/**
 * @author lang : 2023-06-11
 */
class _Page extends _Native {
    /*
     * Normalize pageData in framework
     * {
     *      "list": [],
     *      "count": xx
     * }
     * Normalize old/new data in framework
     */

    public static JsonObject pageData() {
        return ToWeb.pageData(new JsonArray(), 0L);
    }

    public static JsonObject pageData(final JsonArray data, final long size) {
        return ToWeb.pageData(data, size);
    }

    public static JsonArray pageData(final JsonObject data) {
        return Ut.valueJArray(data.getJsonArray(KName.LIST));
    }

    public static JsonObject pageData(final JsonObject pageData, final Function<JsonArray, JsonArray> function) {
        return ToWeb.pageData(pageData, function);
    }
}
