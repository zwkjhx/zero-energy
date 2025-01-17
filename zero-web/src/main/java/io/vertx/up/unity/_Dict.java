package io.vertx.up.unity;

import io.horizon.atom.datamation.KDictAtom;
import io.horizon.atom.datamation.KDictConfig;
import io.horizon.atom.datamation.KDictUse;
import io.horizon.atom.datamation.KMap;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-11
 */
class _Dict extends _Debug {

    // -> Dict for caculation
    /*
     * Keep following dict method
     */
    public static ConcurrentMap<String, KDictUse> dictUse(final JsonObject epsilon) {
        return ServiceDict.dictUse(epsilon);
    }

    public static Future<ConcurrentMap<String, JsonArray>> dictData(final KDictConfig dict, final MultiMap paramsMap) {
        return ServiceDict.dictData(dict, paramsMap);
    }

    public static Future<KDictAtom> dictAtom(final KDictConfig dict, final MultiMap params,
                                             final KMap mapping, final String identifier) {
        return ServiceDict.dictAtom(dict, params, mapping, identifier);
    }

    public static <T> Future<T> dictTo(final T record, final KDictAtom fabric) {
        return ServiceDict.dictTo(record, fabric);
    }
}
