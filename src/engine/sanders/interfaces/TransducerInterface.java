package engine.sanders.interfaces;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;

public interface TransducerInterface {

	
	void register(TReceiver toRegister, TChannel channel);
	
	void fireEvent(TChannel channel, TEvent event, Object[] args);
	
}
