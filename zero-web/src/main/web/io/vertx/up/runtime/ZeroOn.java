package io.vertx.up.runtime;

import io.horizon.fn.Actuator;
import io.horizon.uca.boot.KPivot;
import io.macrocosm.specification.app.HAmbient;
import io.macrocosm.specification.app.HPre;
import io.macrocosm.specification.app.HRegistry;
import io.macrocosm.specification.boot.HOn;
import io.macrocosm.specification.config.HConfig;
import io.macrocosm.specification.program.HArk;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.configuration.BootStore;
import io.vertx.up.eon.configure.YmlCore;
import io.vertx.up.fn.Fn;
import io.vertx.up.util.Ut;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.aeon.refine.Ho.LOG;

/**
 * Arcane:神秘的，秘密的（被替换的原始类名）
 * Zero新版启动器，新的初始化流程发生了变动，直接更改成了如下流程
 * <pre><code>
 *     1. {@link io.horizon.uca.boot.KLauncher} 构造
 *        内部流程：
 *        - {@link io.horizon.spi.BootIo} SPI 连接
 *        - 初始化环境变量
 *        - 提取 {@link io.macrocosm.specification.boot.HOn} 构造配置扫描器
 *        - 执行 {@link HOn#configure} 初始化 {@link io.macrocosm.specification.config.HConfig}
 *     2. 完成 WebServer 的创建过程（此处 WebServer 是主框架的容器实例）
 *        - Zero 中 Vertx 实例
 *        - OSGI 中 Framework 实例
 * </code></pre>
 * 上述步骤完成后调用此类中的方法，做静态启动流程（此处启动流程为共享流程，依旧处于初始化阶段）
 *
 * @author lang : 2023-06-10
 */
public class ZeroOn {
    private static final String MSG_EXT_COMPONENT = "Extension components initialized {0}";
    private static final String MSG_EXT_CONFIGURATION = "Extension configuration missing {0}";

    private static final List<HRegistry.Mod<Vertx>> REGISTERS = new Vector<>();

    public static BiConsumer<Vertx, HConfig> webFn(final BiConsumer<Vertx, HConfig> endFn) {
        // 1. 环境注册
        return (vertx, config) -> registryStart(vertx, config)


            // 2. 是否执行扩展，调用 Fn.passion 带顺序
            .compose(arkSet -> whenExtension(config, () -> Fn.passion(Boolean.TRUE,
                // 2.1. 扩展：配置
                (arks) -> registryAmbient(vertx, config, arkSet),
                // 2.2. 扩展：初始化
                (arks) -> registryArk(vertx, config, arkSet))
            ))


            // 3. 注册结束之后的统一回调
            .onComplete(registryEnd(() -> endFn.accept(vertx, config)));
    }

    /**
     * 「Vertx启动后」扩展流程二
     * <pre><code>
     * 必须在Extension启动完成后执行，且作为非标准化的扩展启动部分
     * - 1. 多应用管理平台
     * - 2. 多租户管理平台
     * - 3. 多语言管理平台
     *
     * </code></pre>
     */
    public static Future<Set<HArk>> registryStart(final Vertx vertx, final HConfig config) {
        final KPivot<Vertx> pivot = KPivot.of(vertx);
        return pivot.registryAsync(config);
    }

    public static Handler<AsyncResult<Boolean>> registryEnd(final Actuator actuator) {
        return res -> {
            if (res.succeeded()) {
                final boolean registered = res.result();
                if (registered) {
                    actuator.execute();
                } else {
                    // 异常退出
                    System.exit(1);
                }
            } else {
                final Throwable ex = res.cause();
                if (Objects.nonNull(ex)) {
                    ex.printStackTrace();
                }
                // 异常退出
                System.exit(1);
            }
        };
    }

    public static Future<Boolean> whenExtension(final HConfig config, final Supplier<Future<Boolean>> supplier) {
        /*
         * 新版不再支持旧模式注册：
         * init:
         *    extension:
         *      - component: xxx
         *    compile:
         *      - component: xxx
         *        order: 1
         * 全流程执行
         * 1. (PlugIn) 配置之前
         *    执行模块配置
         * 2. (PlugIn）初始化之前
         *    执行模块初始化
         */
        final BootStore store = BootStore.singleton();
        if (!store.isInit()) {
            LOG.Env.info(ZeroOn.class, MSG_EXT_CONFIGURATION, config);
            return Future.succeededFuture(Boolean.TRUE);
        }
        if (!ZeroStore.is(YmlCore.init.__KEY)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        return supplier.get();
    }

    /*
     * 「Vertx启动后」扩展流程三 / 配置
     */
    public static Future<Boolean> registryAmbient(final Vertx vertx, final HConfig config, final Set<HArk> arkSet) {
        return registryAsync(config, pre -> pre.beforeModAsync(vertx, config, arkSet), () -> {
            final HAmbient ambient = KPivot.running();
            final List<HRegistry.Mod<Vertx>> registers = registerComponent();
            final List<Future<Boolean>> futures = new ArrayList<>();
            registers.parallelStream()
                .map(register -> register.configureAsync(vertx, ambient))
                .forEach(futures::add);
            return Fn.combineB(futures);
        });
    }

    /*
     * 「Vertx启动后」扩展流程三 / 初始化
     */
    public static Future<Boolean> registryArk(final Vertx vertx, final HConfig config, final Set<HArk> arkSet) {
        return registryAsync(config, pre -> pre.beforeModAsync(vertx, config, arkSet), () -> {
            final List<Future<Boolean>> futures = new ArrayList<>();
            /*
             * 双模式矩阵初始化展开成笛卡尔积
             * Set<HArk> -> HArk
             * List<HRegistry.Mod<Vertx>> -> HRegistry.Mod<Vertx>
             */
            final List<HRegistry.Mod<Vertx>> registers = registerComponent();
            arkSet.parallelStream().forEach(ark -> registers.parallelStream()
                .map(register -> register.initializeAsync(vertx, ark))
                .forEach(futures::add));
            return Fn.combineB(futures);
        });
    }

    private static Future<Boolean> registryAsync(
        final HConfig config,
        final Function<HPre<Vertx>, Future<Boolean>> runnerPre, final Supplier<Future<Boolean>> runner) {
        final Class<?> implCls = config.pre();
        if (Objects.isNull(implCls)) {
            return runner.get();
        }
        final HPre<Vertx> pre = Ut.singleton(implCls);
        if (Objects.isNull(pre)) {
            return runner.get();
        }
        return runnerPre.apply(pre).compose(nil -> runner.get());
    }

    /**
     * 提取模块构造器，提取过程中针对静态变量执行同步和初始化，每个程序只执行一次
     *
     * @return 模块构造器列表
     */
    private static List<HRegistry.Mod<Vertx>> registerComponent() {
        if (REGISTERS.isEmpty()) {
            final JsonObject initConfig = ZeroStore.option(YmlCore.init.__KEY);
            LOG.Env.info(ZeroOn.class, MSG_EXT_COMPONENT, initConfig.encode());
            // 1. nativeComponent first
            final JsonArray bridges = Ut.valueJArray(initConfig, YmlCore.init.CONFIGURE);
            // 2. 针对每个组件的统一初始化
            Ut.itJArray(bridges)
                .map(json -> {
                    final String className = json.getString(YmlCore.init.configure.COMPONENT);
                    return Ut.clazz(className, null);
                })
                .filter(Objects::nonNull)
                .filter(clazz -> Ut.isImplement(clazz, HRegistry.Mod.class))
                .forEach(instance -> REGISTERS.add(Ut.singleton(instance)));
        }
        // 3. 组件收齐，执行初始化
        return REGISTERS;
    }
}
