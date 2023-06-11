package io.vertx.up.unity;

import io.modello.specification.HRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.eon.KName;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author lang : 2023-06-11
 */
class _Update extends _To {

    /*
     * Update Data on Record
     * 1. Generic T ( Pojo )
     * 2. List<T>
     * 3. JsonObject
     * 4. JsonArray
     * 5. Record
     * 6. Record[]
     */
    public static <T> T cloneT(final T input) {
        return CompareT.cloneT(input);
    }

    public static <T> T updateT(final T query, final JsonObject params) {
        return CompareT.updateT(query, params);
    }

    public static <T> List<T> updateT(final List<T> query, final JsonArray params) {
        return CompareT.updateT(query, params, KName.KEY);
    }

    public static <T> List<T> updateT(final List<T> query, final JsonArray params, final String field) {
        return CompareT.updateT(query, params, field);
    }

    public static JsonArray updateJ(final JsonArray query, final JsonArray params) {
        return CompareT.updateJ(query, params, KName.KEY);
    }

    public static JsonArray updateJ(final JsonArray query, final JsonArray params, final String field) {
        return CompareT.updateJ(query, params, field);
    }

    public static HRecord updateR(final HRecord record, final JsonObject params) {
        return CompareT.updateR(record, params, () -> UUID.randomUUID().toString());
    }

    public static HRecord[] updateR(final HRecord[] record, final JsonArray array) {
        return updateR(record, array, KName.KEY);
    }

    public static HRecord[] updateR(final HRecord[] record, final JsonArray array, final String field) {
        final List<HRecord> recordList = Arrays.asList(record);
        return CompareT.updateR(recordList, array, field).toArray(new HRecord[]{});
    }
}
