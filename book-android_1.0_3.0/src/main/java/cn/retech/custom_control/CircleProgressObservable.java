package cn.retech.custom_control;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public final class CircleProgressObservable extends Observable {
	private final String TAG = this.getClass().getSimpleName();
	public static final CircleProgressObservable INSTANCE = new CircleProgressObservable();
	private final Timer timer = new Timer();

	private CircleProgressObservable() {
	}

	public void start() {
		timer.schedule(new TimerTask() {
			public void run() {
				setChanged();
				notifyObservers();
			}
		}, 0, 25);
	}

	public void stop() {
		timer.cancel();
	}
}
