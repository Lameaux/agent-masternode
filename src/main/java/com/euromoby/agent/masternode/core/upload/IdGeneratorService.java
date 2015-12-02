package com.euromoby.agent.masternode.core.upload;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.euromoby.agent.utils.Base62;

@Component
public class IdGeneratorService {

	private final AtomicLong idGenerator;

	public IdGeneratorService() {
		Calendar cal = Calendar.getInstance();
		cal.set(2015, 0, 0, 0, 0, 0);
		long startOf2015 = (cal.getTimeInMillis() / 1000) * 1000;
		long startingPoint = (System.currentTimeMillis() - startOf2015) * 1000;
		idGenerator = new AtomicLong(startingPoint);
	}
	
	public String generateId() {
		long id = idGenerator.incrementAndGet();
		return Base62.encodeBase10(id);
	}

}
