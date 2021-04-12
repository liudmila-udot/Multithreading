# Java Multithreading
## Wait, notify, notifyAll

Belongs to Object methods.

These methods should only be called by a thread that is the owner of this object's monitor. A thread becomes the owner of the object's monitor in one of three ways:

* By executing a synchronized instance method of that object.
* By executing the body of a synchronized statement that synchronizes on the object.
* For objects of type Class, by executing a synchronized static method of that class.

```
synchronized (object){
    object.wait();
}
```
**Wait**: _executing thread releases the lock and come into object's wait-set_.

```
synchronized (object) {
  object.notify();
}
```
**Notify**: wakes up a single thread that is waiting on this object's monitor. 
If any threads are waiting on this object, one of them is chosen to be awakened. 
The choice is arbitrary and occurs at the discretion of the implementation. 

_Notify() itself doesn't release the monitor it holds._
The awakened thread will not be able to proceed until the current thread relinquishes the lock on this object. 

```
synchronized (object) {
  object.notifyAll();
}
```

**NotifyAll**: Wakes up all threads that are waiting on this object's monitor. 
The awakened threads will compete in the usual manner with any other threads that might be actively competing to synchronize on this object; _for example, the awakened threads enjoy no reliable privilege or disadvantage in being the next thread to lock this object._

All methods throw IllegalMonitorStateException - if the current thread is not the owner of this object's monitor.

## Sleep, Yield

 _static_ methods of Thread class methods.
 
 **Sleep** (millis): 
 Causes the currently executing thread to sleep. The thread does not lose ownership of any monitors.
 
 **Yield**:
 A hint to the scheduler that the current thread is willing to yield its current use of a processor. The scheduler is free to ignore this hint.
 It is rarely appropriate to use this method. It may be useful for debugging or testing purposes, where it may help to reproduce bugs due to race conditions. 
 
  
 
 
 

