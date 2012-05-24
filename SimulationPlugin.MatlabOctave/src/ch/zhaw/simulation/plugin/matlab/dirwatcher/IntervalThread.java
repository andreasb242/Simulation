package ch.zhaw.simulation.plugin.matlab.dirwatcher;

/**
 * @author: bachi
 */
public abstract class IntervalThread implements Runnable{

	private boolean running;
	private int interval;
	private Thread thread;
	
	public IntervalThread(int interval) {
		this.running = false;
		this.interval = interval;
		this.thread = null;
	}
	
	public void start() {
		if (thread != null) {
			while (thread.getState() != Thread.State.TERMINATED) {
				running = false;
				try  {
					thread.join(1000);
				} catch (InterruptedException e) {
					//
				}
			}
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		running = false;
	}

	@Override
	public void run() {
		while (running) {
			try {
				doInterval();
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				 //
			}
		}
	}

	public abstract void doInterval();
}
