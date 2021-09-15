package taskManager.utility;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RequestManager {
	private final Lock				_lock;
	private final Condition			_condition;
	private String					_response;
	
	public RequestManager() {
		_lock		= new ReentrantLock();
		_condition	= _lock.newCondition();
	}
	
	public void setResponse(String response) {
		_lock.lock();

		try {
			_response = response;
			_condition.signalAll();
		} finally {
			_lock.unlock();
		}
	}
	
	public String getResponse() {
		_lock.lock();
	
		try {
			while (_response == null) {
				try {
					_condition.await();
				} catch(InterruptedException ignore) {}
			}
			return _response;
		} finally {
			_lock.unlock();
		}
	}
}

