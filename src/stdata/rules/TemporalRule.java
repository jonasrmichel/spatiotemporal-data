package stdata.rules;

import java.util.Timer;
import java.util.TimerTask;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class TemporalRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends Rule<G, E, F> {
	/** The rule's timer. */
	Timer timer = null;

	public TemporalRule(G baseGraph, E eventGraph, F framedGraph, long delay, long period) {
		super(baseGraph, eventGraph, framedGraph);

		start(delay, period);
	}

	public TemporalRule(G baseGraph, E eventGraph, F framedGraph, IRuleDelegate delegate,
			long delay, long period) {
		super(baseGraph, eventGraph, framedGraph, delegate);

		start(delay, period);
	}

	/**
	 * Called when the rule's timer expires.
	 */
	public abstract void callback();

	/**
	 * Creates a new timer that schedules the rule's callback for repeated
	 * fixed-delay execution, beginning after the specified delay. Subsequent
	 * executions take place at approximately regular intervals separated by the
	 * specified period.
	 * 
	 * @param delay
	 *            delay in milliseconds before task is to be executed.
	 * @param period
	 *            time in milliseconds between successive task executions.
	 */
	public void start(long delay, long period) {
		if (timer != null)
			stop();

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				callback();
			}

		}, delay, period);
	}

	/**
	 * Cancels and purges the rule's timer.
	 */
	public void stop() {
		timer.cancel();
		timer.purge();
	}

}
