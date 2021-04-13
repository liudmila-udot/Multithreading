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
 
 ## Join
 
Method of Thread class.

Waits for this thread to die.
This java thread join method puts the current thread on wait until the thread on which it’s called is dead.

```
        Thread t1 = new Thread(new MyRunnable(), "t1");
        Thread t2 = new Thread(new MyRunnable(), "t2");
        
        t1.start();
        
        //start second thread after waiting for 2 seconds or if it's dead
        try {
            t1.join(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        t2.start();
```

## Java Memory Model

http://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html

At the processor level, a memory model defines necessary and sufficient conditions for knowing that writes to memory by other processors are visible to the current processor, and writes by the current processor are visible to other processors. 
Java includes several language constructs, including volatile, final, and synchronized, which are intended to help the programmer describe a program's concurrency requirements to the compiler. The Java Memory Model defines the behavior of volatile and synchronized, and, more importantly, ensures that a correctly synchronized Java program runs correctly on all processor architectures.

https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html
Two actions can be ordered by a happens-before relationship. If one action happens-before another, then the first is visible to and ordered before the second.

* An unlock on a monitor happens-before every subsequent lock on that monitor.

* A write to a volatile field (§8.3.1.4) happens-before every subsequent read of that field.

* A call to start() on a thread happens-before any actions in the started thread.

* All actions in a thread happen-before any other thread successfully returns from a join() on that thread.

* The default initialization of any object happens-before any other actions (other than default-writes) of a program.

When a program contains two conflicting accesses (§17.4.1) that are not ordered by a happens-before relationship, it is said to contain a **data race**.

In the absence of a happens before ordering between two operations, the JVM is free to reorder them as it pleases
https://stackoverflow.com/questions/16213443/instruction-reordering-happens-before-relationship-in-java#:~:text=In%20the%20absence%20of%20a,on%20that%20same%20monitor%20lock.

```
T1:

x = 5;
y = 6;
```
```
T2:

if (y == 6) System.out.println(x);
```
From T1's perspective, an execution must be consistent with y being assigned after x (program order). However from T2's perspective this does not have to be the case and T2 might print 0.

T1 is actually allowed to assign y first as the 2 assignements are independent and swapping them does not affect T1's execution.

With proper synchronization, T2 will always print 5 or nothing.

Marking a field as final forces the compiler to complete initialization of the field before the constructor completes. There is no such guarantee however for non-final fields. This might seem weird, however there are many things done by the compiler and JVM for optimization purposes such as reordering instructions, that cause such stuff to occur.

**Non-atomic Treatment of double and long**
For the purposes of the Java programming language memory model, a single write to a non-volatile long or double value is treated as two separate writes: one to each 32-bit half. This can result in a situation where a thread sees the first 32 bits of a 64-bit value from one write, and the second 32 bits from another write.

Writes and reads of volatile long and double values are always atomic.

 
 
 

