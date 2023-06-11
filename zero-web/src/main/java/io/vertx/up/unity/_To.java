package io.vertx.up.unity;

import io.horizon.atom.program.KRef;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.up.atom.worker.Mission;
import io.vertx.up.commune.Envelop;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2023-06-11
 */
class _To extends _Rule {
    /*
     * Entity ( Pojo ) to JsonObject, support pojo file here
     * 1) toJson / fromJson
     * 2) toZip:  Toggle switch from interface style to worker here, the key should be "0", "1", "2", "3", ....
     * 3) toJArray
     * ( Business Part, support `pojoFile` conversation )
     * 4) toFile
     */

    public static <T> JsonObject toJson(final T entity) {
        return ToCommon.toJObject(entity, "");
    }

    public static <T> JsonObject toJson(final T entity, final String pojo) {
        return ToCommon.toJObject(entity, pojo);
    }

    public static <T> JsonArray toJson(final List<T> list) {
        return ToCommon.toJArray(list, "");
    }

    public static <T> JsonArray toJson(final List<T> list, final String pojo) {
        return ToCommon.toJArray(list, pojo);
    }

    public static JsonObject toZip(final Object... args) {
        return ToCommon.toToggle(args);
    }

    /*
     * Analyze the arguments by type
     * 1) used by `io.vertx.up.container.mime.parse.TypedAtomic`.
     * 2) used by `io.vertx.up.uca.invoker.InvokerUtil`.
     * The arguments are different, but could support more method declare
     */
    // Job
    public static Object toParameter(final Envelop envelop, final Class<?> type, final Mission mission, final KRef underway) {
        return ToWeb.toParameter(envelop, type, mission, underway);
    }

    // Worker
    public static Object toParameter(final Envelop envelop, final Class<?> type) {
        return ToWeb.toParameter(envelop, type);
    }

    // Agent
    public static Object toParameter(final RoutingContext context, final Class<?> type) {
        return ToWeb.toParameter(context, type);
    }

    /**
     * File upload tool to convert data
     *
     * @param fileUploads Set of file uploads
     * @param expected    The method declared type
     * @param consumer    File consumer to read `filename` to Buffer
     * @param <T>         Returned type for declared
     *
     * @return T reference that converted
     */
    public static <T> T toFile(final Set<FileUpload> fileUploads, final Class<?> expected, final Function<String, Buffer> consumer) {
        return Upload.toFile(fileUploads, expected, consumer);
    }

    /**
     * Single file upload converting
     *
     * @param fileUpload The `FileUpload` reference
     * @param expected   The method declared type
     * @param consumer   File consumer to read `filename` to Buffer
     * @param <T>        Returned type of declared
     *
     * @return T reference that converted
     */
    public static <T> T toFile(final FileUpload fileUpload, final Class<?> expected, final Function<String, Buffer> consumer) {
        return Upload.toFile(fileUpload, expected, consumer);
    }

    /**
     * Split `Set<FileUpload>` by fieldname
     *
     * @param fileUploads FileUpload Set
     *
     * @return Map of `field = Set<FileUpload>`
     */
    public static ConcurrentMap<String, Set<FileUpload>> toFile(final Set<FileUpload> fileUploads) {
        return Upload.toFile(fileUploads);
    }
}
