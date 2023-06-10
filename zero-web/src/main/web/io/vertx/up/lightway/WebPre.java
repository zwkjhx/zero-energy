package io.vertx.up.lightway;

import io.macrocosm.specification.app.HPre;
import io.macrocosm.specification.config.HConfig;
import io.macrocosm.specification.program.HArk;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.up.configuration.BootStore;
import io.vertx.up.plugin.jooq.JooqInfix;
import io.vertx.up.plugin.shared.MapInfix;
import io.vertx.up.unity.Ux;

import java.util.Set;

/**
 * @author lang : 2023-06-10
 */
public class WebPre implements HPre<Vertx> {


    /**
     * 「Vertx启动后」（同步）扩展流程一
     * <p>
     * 流程一：Vertx原生插件初始化，带Vertx的专用启动流程，在Vertx实例启动之后启动
     * <pre><code>
     *         1. SharedMap提前初始化（Infix架构下所有组件的特殊组件预启动流程）
     *         2. 其他Native插件初始化
     *     </code></pre>
     * </p>
     *
     * @param vertx   Vertx实例
     * @param options 启动配置
     */
    @Override
    public Boolean beforeStart(final Vertx vertx, final JsonObject options) {
        final BootStore store = BootStore.singleton();
        if (store.isShared()) {
            /*
             * MapInfix作为初始化容器过程中第一个必须要使用的组件，只要系统重启用了它，那么就必须在
             * 容器启动之前执行初始化，特别是针对缓存数据会在实现过程中存在，此缓存数据用在如下位置
             * 1. 扩展模块配置中
             * 2. 扩展模块初始化中
             * 所有 Infix 不惧怕多次重复加载，本身具有幂等性操作。
             */
            MapInfix.init(vertx);
        }
        return Boolean.TRUE;
    }

    @Override
    public Future<Boolean> beforeInitAsync(final Vertx vertx, final HConfig config, final Set<HArk> arkSet) {
        // 初始化之前必须做的基础环境准备，现阶段需准备的主要如下：
        // Fix: [ ERR-40065 ] ( JooqInfix ) Booting Error: (Jooq) Jooq database configuration pool key = `provider`
        // missing, its required when you enable Jooq
        JooqInfix.init(vertx);
        return Ux.futureT();
    }
}
