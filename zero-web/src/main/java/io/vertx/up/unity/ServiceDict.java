package io.vertx.up.unity;

import io.horizon.spi.component.Dictionary;
import io.horizon.uca.cache.Cc;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.horizon.atom.datamation.KDictAtom;
import io.horizon.atom.datamation.KDictConfig;
import io.vertx.up.uca.adminicle.FieldMapper;
import io.vertx.up.util.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * DictSource
 * Dict
 * DictEpsilon
 */
@SuppressWarnings("all")
class ServiceDict {
    private static final Cc<Integer, Dictionary> CC_DICT = Cc.open();

    static <T> Future<T> dictTo(final T record, final KDictAtom fabric) {
        final FieldMapper mapper = new FieldMapper();
        if (record instanceof JsonObject) {
            final JsonObject ref = (JsonObject) record;
            return fabric.inTo(ref)
                .compose(processed -> Ux.future(mapper.out(processed, fabric.mapping())))
                .compose(processed -> Ux.future((T) processed));
        } else if (record instanceof JsonArray) {
            final JsonArray ref = (JsonArray) record;
            return fabric.inTo(ref)
                .compose(processed -> Ux.future(mapper.out(processed, fabric.mapping())))
                .compose(processed -> Ux.future((T) processed));
        } else {
            return Ux.future(record);
        }
    }

    static Future<ConcurrentMap<String, JsonArray>> dictCalc(final KDictConfig dict, final MultiMap paramMap) {
        if (Objects.isNull(dict)) {
            /*
             * Not `Dict` configured
             */
            return ToCommon.future(new ConcurrentHashMap<>());
        } else {
            /*
             * Dict extract here
             */
            final ConcurrentMap<String, JsonArray> dictData = new ConcurrentHashMap<>();
            if (dict.valid()) {
                /*
                 * Component Extracted
                 */
                final Class<?> dictCls = dict.getComponent();
                if (Ut.isImplement(dictCls, Dictionary.class)) {
                    /*
                     * JtDict instance for fetchAsync
                     */
                    final Dictionary dictStub = CC_DICT.pick(() -> Ut.instance(dictCls), dict.hashCode());
                    // Fn.po?l(POOL_DICT, dict.hashCode(), () -> Ut.instance(dictCls));
                    /*
                     * Param Map / List<Source>
                     */
                    return dictStub.fetchAsync(paramMap, dict.getSource());
                } else return ToCommon.future(dictData);
            }
            return ToCommon.future(dictData);
        }
    }
}
