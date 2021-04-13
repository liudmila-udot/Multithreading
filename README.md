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
A thread can wake up without being notified, interrupted, or timing out, a so-called spurious wakeup.
The recommended approach to waiting is to check the condition being awaited in a while loop around the call to wait, as shown in the example below. Among other things, this approach avoids problems that can be caused by spurious wakeups.

```
synchronized (obj) {
         while (<condition does not hold> and <timeout not exceeded>) {
             long timeoutMillis = ... ; // recompute timeout values
             int nanos = ... ;
             obj.wait(timeoutMillis, nanos);
         }
         ... // Perform action appropriate to condition or timeout
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

## What is reentrant in Java?
https://stackoverflow.com/questions/16504231/what-is-the-meaning-of-reentrantlock-in-java
The Java runtime system allows a thread to re-acquire a monitor that it already holds because Java monitors are reentrant. Reentrant monitors are important because they eliminate the possibility of a single thread deadlocking itself on a monitor that it already holds.

class ReentrantTester {

```
    public synchronized void methodA() {
      System.out.println("Now I am inside methodA()");
      methodB();
    }

    public synchronized void methodB() {
      System.out.println("Now I am inside methodB()");
    }

    public static void main(String [] args) {
        ReentrantTester rt = new ReentrantTester();
        rt.methodA();  
    }

}
```

The output is :

```
Now I am inside methodA()
Now I am inside methodB()
```

## ReentrantLock

https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html
https://stackoverflow.com/questions/11821801/why-use-a-reentrantlock-if-one-can-use-synchronizedthis

A reentrant mutual exclusion Lock with the same basic behavior and semantics as the implicit monitor lock accessed using synchronized methods and statements, but with extended capabilities.

It is recommended practice to always immediately follow a call to lock with a try block, most typically in a before/after construction such as:

```
 class X {
   private final ReentrantLock lock = new ReentrantLock();
   // ...

   public void m() {
     lock.lock();  // block until condition holds
     try {
       // ... method body
     } finally {
       lock.unlock()
     }
   }
 }
```

Benefits:

* A ReentrantLock is unstructured, unlike synchronized constructs -- i.e. you don't need to use a block structure for locking and can even hold a lock across methods. An example:
```
private ReentrantLock lock;

public void foo() {
  ...
  lock.lock();
  ...
}

public void bar() {
  ...
  lock.unlock();
  ...
}
```

* ReentrantLock supports lock polling
  _public boolean tryLock_
  
  If the lock is held by another thread then this method will return immediately with the value false.
 
 * configurable fairness policy
 _The constructor for this class accepts an optional fairness parameter. When set true, under contention, locks favor granting access to the longest-waiting thread. Otherwise this lock does not guarantee any particular access order. Programs using fair locks accessed by many threads may display lower overall throughput (i.e., are slower; often much slower) than those using the default setting, but have smaller variances in times to obtain locks and guarantee lack of starvation. Note however, that fairness of locks does not guarantee fairness of thread scheduling. Thus, one of many threads using a fair lock may obtain it multiple times in succession while other active threads are not progressing and not currently holding the lock. Also note that the untimed tryLock method does not honor the fairness setting. It will succeed if the lock is available even if other threads are waiting._
 
 **When should you use ReentrantLocks?** According to that developerWorks article...
 
 The answer is pretty simple -- use it when you actually need something it provides that synchronized doesn't, li

## Deadlock

Deadlock describes a situation where two or more threads are blocked forever, waiting for each other. 

## Starvation and Livelock

https://docs.oracle.com/javase/tutorial/essential/concurrency/starvelive.html

Starvation and livelock are much less common a problem than deadlock, but are still problems that every designer of concurrent software is likely to encounter.

**Starvation**
Starvation describes a situation where a thread is unable to gain regular access to shared resources and is unable to make progress. This happens when shared resources are made unavailable for long periods by "greedy" threads. For example, suppose an object provides a synchronized method that often takes a long time to return. If one thread invokes this method frequently, other threads that also need frequent synchronized access to the same object will often be blocked.

**Livelock**
A thread often acts in response to the action of another thread. If the other thread's action is also a response to the action of another thread, then livelock may result. As with deadlock, livelocked threads are unable to make further progress. However, the threads are not blocked — they are simply too busy responding to each other to resume work. This is comparable to two people attempting to pass each other in a corridor: Alphonse moves to his left to let Gaston pass, while Gaston moves to his right to let Alphonse pass. Seeing that they are still blocking each other, Alphone moves to his right, while Gaston moves to his left. They're still blocking each other, so...

## Semaphore
A counting semaphore. Conceptually, a semaphore maintains a set of permits. Each acquire() blocks if necessary until a permit is available, and then takes it. Each release() adds a permit, potentially releasing a blocking acquirer. 

_public void acquire()_
             throws InterruptedException
Acquires a permit from this semaphore, blocking until one is available, or the thread is interrupted.

_public boolean tryAcquire()_
Acquires a permit from this semaphore, only if one is available at the time of invocation.
Acquires a permit, if one is available and returns immediately, with the value true, reducing the number of available permits by one.

If no permit is available then this method will return immediately with the value false.

_public void release()_
Releases a permit, returning it to the semaphore.

## Atomics

All classes have get and set methods that work like reads and writes on volatile variables. That is, a set has a happens-before relationship with any subsequent get on the same variable. 

```
class SynchronizedCounter {
    private int c = 0;

    public synchronized void increment() {
        c++;
    }

    public synchronized void decrement() {
        c--;
    }

    public synchronized int value() {
        return c;
    }

}
```

For this simple class, synchronization is an acceptable solution. But for a more complicated class, we might want to avoid the liveness impact of unnecessary synchronization. Replacing the int field with an AtomicInteger allows us to prevent thread interference without resorting to synchronization, as in AtomicCounter:

```
import java.util.concurrent.atomic.AtomicInteger;

class AtomicCounter {
    private AtomicInteger c = new AtomicInteger(0);

    public void increment() {
        c.incrementAndGet();
    }

    public void decrement() {
        c.decrementAndGet();
    }

    public int value() {
        return c.get();
    }

}
```

https://en.wikipedia.org/wiki/Compare-and-swap
In computer science, compare-and-swap (CAS) is an atomic instruction used in multithreading to achieve synchronization. It compares the contents of a memory location with a given value and, only if they are the same, modifies the contents of that memory location to a new given value. This is done as a single atomic operation. The atomicity guarantees that the new value is calculated based on up-to-date information; if the value had been updated by another thread in the meantime, the write would fail. The result of the operation must indicate whether it performed the substitution; this can be done either with a simple boolean response (this variant is often called compare-and-set), or by returning the value read from the memory location (not the value written to it).

Implementation for AtomicLong

```
public class AtomicLong {

    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public final long getAndIncrement() {
        return unsafe.getAndAddLong(this, valueOffset, 1L);
    }
}

public final class Unsafe {

  public final long getAndAddLong(Object var1, long var2, long var4) {
        long var6;
        do {
            var6 = this.getLongVolatile(var1, var2);
        } while(!this.compareAndSwapLong(var1, var2, var6, var6 + var4));

        return var6;
    }
}
```

## ConcurrentHashMap

https://crunchify.com/hashmap-vs-concurrenthashmap-vs-synchronizedmap-how-a-hashmap-can-be-synchronized-in-java/

You should use ConcurrentHashMap when you need very high concurrency in your project.
* It is thread safe without synchronizing the whole map.
* Reads can happen very fast while write is done with a lock.
* There is no locking at the object level.
* The locking is at a much finer granularity at a hashmap bucket level.
* ConcurrentHashMap doesn’t throw a ConcurrentModificationException if one thread tries to modify it while another is iterating over it.
* ConcurrentHashMap uses multitude of locks..

## ForkJoinPool

https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ForkJoinPool.html

An ExecutorService for running ForkJoinTasks. A ForkJoinPool provides the entry point for submissions from non-ForkJoinTask clients, as well as management and monitoring operations.
A ForkJoinPool differs from other kinds of ExecutorService mainly by virtue of employing work-stealing: all threads in the pool attempt to find and execute subtasks created by other active tasks (eventually blocking waiting for work if none exist). 

ForkJoinPool()
Creates a ForkJoinPool with parallelism equal to Runtime.availableProcessors()