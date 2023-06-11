package io.vertx.up.uca.job.plugin;

import io.horizon.atom.program.KRef;
import io.vertx.core.Future;
import io.vertx.up.commune.Envelop;
import io.vertx.up.unity.Ux;

/*
 * Job income before, this income interface should provide Future<JobIn> to Job to consume
 */
public interface JobIncome {
    /*
     * Async process income here
     */
    Future<Envelop> beforeAsync(final Envelop envelop);

    /*
     * Hidden channel to pass dict data,
     * It's underway data passing from
     * KIncome -> Job -> Outcome
     */
    default Future<KRef> underway() {
        final KRef refer = new KRef();
        return Ux.future(refer);
    }
}
