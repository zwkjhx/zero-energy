package io.vertx.up.unity;


import io.horizon.uca.boot.KPivot;
import io.macrocosm.specification.app.HAmbient;
import io.macrocosm.specification.app.HRegistry;
import io.macrocosm.specification.program.HArk;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.eon.configure.YmlCore;
import io.vertx.up.fn.Fn;
import io.vertx.up.plugin.jooq.JooqInfix;
import io.vertx.up.util.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 初始化专用，用于注册扩展模块并执行对应的初始化方法，通常扩展模块初始化会放在 `vertx-dock.yml` 文件中，格式如下：
 * 旧版格式
 * <p>
 * ```yml
 * // <pre><code>
 * init:
 *   - component: "[ComponentName1]"
 *   - component: "[ComponentName2]"
 * // </code></pre>
 * ```
 * </p>
 * 新版格式
 * <p>
 * ```yml
 * // <pre><code>
 * init:
 *    extension:
 *     - component: "[ComponentName1]"
 *     - component: "[ComponentName2]"
 *    bridge:
 *     - component: "xxxx"
 *     - order: 1
 * // </code></pre>
 * ```
 * </p>
 * 旧版（1.0之前）采用了静态模式的无规则匹配，以 `init` 函数名为主，而新版直接以实现了 {@link io.macrocosm.specification.app.HRegistry.Mod}
 * 接口中的同异步双向注册为主，而新版中包含了两个核心方法
 * <pre><code>
 *     configure: 配置对接，所有应用统一处理
 *     initialize：初始化对接，每个应用单独处理
 * </code></pre>
 *
 * @author lang : 2023-06-09
 */
class Registry {

    static Future<Boolean> registryMod(final Vertx vertx, final JsonObject initConfig, final Set<HArk> arkSet) {
        // 1. nativeComponent first
        final JsonArray bridges = Ut.valueJArray(initConfig, YmlCore.init.CONFIGURE);
        // 2. 针对每个组件的统一初始化
        final List<HRegistry.Mod<Vertx>> registers = new ArrayList<>();
        Ut.itJArray(bridges)
            .map(json -> {
                final String className = json.getString(YmlCore.init.configure.COMPONENT);
                return Ut.clazz(className, null);
            })
            .filter(Objects::nonNull)
            .filter(clazz -> Ut.isImplement(clazz, HRegistry.Mod.class))
            .forEach(instance -> registers.add(Ut.singleton(instance)));
        // 3. 组件收齐，执行初始化
        return registryAmbient(vertx, registers).compose(nil -> {
            // 初始化之前必须做的基础环境准备，现阶段需准备的主要如下：
            // Fix: [ ERR-40065 ] ( JooqInfix ) Booting Error: (Jooq) Jooq database configuration pool key = `provider`
            // missing, its required when you enable Jooq
            JooqInfix.init(vertx);
            return Ux.futureT();
        }).compose(nil -> registryArk(vertx, registers, arkSet));
    }

    private static Future<Boolean> registryAmbient(final Vertx vertx, final List<HRegistry.Mod<Vertx>> registers) {
        final HAmbient ambient = KPivot.running();
        final List<Future<Boolean>> futures = new ArrayList<>();
        registers.parallelStream()
            .map(register -> register.configureAsync(vertx, ambient))
            .forEach(futures::add);
        return Fn.combineB(futures);
    }

    private static Future<Boolean> registryArk(final Vertx vertx, final List<HRegistry.Mod<Vertx>> registers, final Set<HArk> arkSet) {
        final List<Future<Boolean>> futures = new ArrayList<>();
        /*
         * 双模式矩阵初始化展开成笛卡尔积
         * Set<HArk> -> HArk
         * List<HRegistry.Mod<Vertx>> -> HRegistry.Mod<Vertx>
         */
        arkSet.parallelStream().forEach(ark -> registers.parallelStream()
            .map(register -> register.initializeAsync(vertx, ark))
            .forEach(futures::add));
        return Fn.combineB(futures);
    }
}
