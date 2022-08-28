package al.photoBackup.util;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicDouble {
	private final AtomicReference<Double> value;

	public AtomicDouble(double value) {
		this.value = new AtomicReference(value);
	}

	public AtomicDouble() {
		this.value = new AtomicReference(Double.valueOf(0.0));
	}

	public double addAndGet(double delta) {
		while (true) {
			Double currentValue = value.get();
			Double newValue = currentValue + delta;
			if (value.compareAndSet(currentValue, newValue))
				return currentValue;
		}
	}

	public double get() {
		return value.get();
	}
}
