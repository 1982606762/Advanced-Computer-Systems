# ACS 笔记



## Part1-3 （lectures 1-2）

### Chapter1

Introduce fundamental abstractions like memory, interpreters, and communication links.

#### Memory

Memory, sometimes called storage, is the system component that remembers data values for use in computation.

> Durability: A property of a storage medium: the length of time it remembers. 
>
> Stability: A property of an object: it is unchanging.
>
>  Persistence: A property of an active agent: it keeps trying.

API:

* Write(name,value)
* Value <- Read(name)

read/write coherence: the result of the read of a named cell is always the same as the most recent write to that cell.

Before-or-after atomicity: the result of every read or write is as if that read or write occurred either completely before or completely after any other read or write.

### Chapter 2

* Recognize and explain modular designs with clients and services.
* Predict the functioning of service calls under diﬀerent RPC semantics and failure modes.
* Identify diﬀerent mechanisms to achieve RPCs.
* Implement RPC services with an appropriate mechanism, such as web services.

#### RPC

using stub to make remote procedure calls behave the same as an ordinary procedure call.

Difference between ordinary procedure call: 

1. RPCs can reduce fate sharing between the caller and callee by exposing the failures of the callee to the caller so that the caller can recover. 
2. RPCs introduce a new failure mode
3. calling a local procedure takes typically much less time than calling a remote procedure call.
4. some programming language features don’t combine well with RPC.

#### RPC strategies:

In order to handle no-response case.

At-least-once

> If the client stub doesn’t receive a response within some specific time, the stub resends the request as many times as necessary until it receives a response from the service.

At-most-once 

> If the client stub doesn’t receive a response within some specific time, then the client stub returns an error to the caller, indicating that the service may or may not have processed the request.
>
> Implementing at-most-once RPC is harder than it sounds because the underlying network may duplicate the request message without the client stub’s knowledge.

Exactly-once

> These semantics are the ideal, but because the client and service are independent it is in principle impossible to guarantee.
>
> by adding the complexity of extra message exchanges and careful recordkeeping, one can approach exactly-once semantics closely enough to satisfy some applications.

#### Communicating through an Intermediary

e.g. mail service

### Chapter 3

* Explain performance metrics such as latency, throughput, overhead, utilization, capacity, and scalability.
* List common hardware parameters that aﬀect performance.
* Apply performance improvement techniques, such as concurrency, batching, dallying, and fast-path coding.

#### Bottleneck source

1. Physical reasons may cause bottleneck
2. several clients may share a device

#### Performance Metrics

Capacy : service's size or amount of resources.

Utilization(利用率): percentage of capacity of a resource that is used for some given workload of requests.

Overhead(开销) : what the layers below current layer do

Useful work : what the layers above it do

处理器利用了95%，但是只给应用层了70%的利用，因为操作系统层使用了25%。此时应用层就认为开销是25%，70%是有用工作。

Latency : the delay between a change at the input to a system and the corresponding change at its output.(输入和输出的时延)

Have a pipeline model in mind to think about latency.

Throughput(吞吐量) : a measure of the rate of useful work done by a service for some given workload of requests.

Throughput = 1/latency

假如管道有两部分，第一部分每秒1000k，第二部分每秒100k，总吞吐量小于等于100k，也就是t(a+b) <= min(t(a),t(b))

假如串行处理吞吐量就和延迟有关，并行处理的话没有直接关系。

#### Apply performance improvement techniques

##### Fast-path coding

splitting the staged pipeline into a fast path for the frequent requests and a slow path for other requests

* One optimized path for common requests ->  fast path
* One slow but comprehensive path for all other requests -> slow path
* Caching is an example

It can lead to the optimization for the common case.

##### Concurrency

this plan can, in principle, speed up the processing by a factor n

In practice, the speedup is usually less than n because there is overhead in parallelizing a computation—the subtasks need to communicate with each other,

Challenges:

* many applications are difficult to parallelize. Applications such as search have exploitable parallelism, but other computations don’t split easily into n mostly independent pieces.
* developing parallel applications is difficult because the programmer must manage the concurrency and coordinate the activities of the different subtasks.

Traditionally: rely on continuous technology improvements to reduce application latency.

This development means that improving performance by using concurrency will inevitably increase in importance.

##### Batching

Run multiple requests at once.

May improve latency and throughput

##### Dallying

Wait until you accumulate some requests and then run them.

May improve throughput when used together with batching, but typically incurs a latency penalty

## Part4(lecture 3-4) Concurrency Control

* Identify the multiple interpretations of the property of atomicity.
* Implement methods to ensure before-or-after atomicity, and argue for their correctness.
* Explain the variants of the two-phase locking (2PL) protocol, in particular the widely-used Strict 2PL.
* Discuss deﬁnitions of serializability and their implications, in particular conﬂict-serializability and view-serializability.
* Apply the conﬂict-serializability test using a precedence graph to transaction schedules.
* Explain deadlock prevention and detection techniques.
* Apply deadlock detection using a waits-for graph to transaction schedules.
* Explain situations where predicate locking is required.
* Explain the optimistic concurrency control and multi-version concurrency control models.
* Predict validation decisions under optimistic concurrency control.

transaction: any one execution of a user program in a DBMS

Schedule: a list of actions (reading, writing, aborting, or committing) from a set of transactions

serializable schedule: effect on any consistent database instance is guaranteed to be identical to that of some complete serial schedule over S.



### THE ACID PROPERTIES

1. atomic: Either all actions are carried out or none are.
2. consistency: Each transaction, run by itself with no concurrent execution of other transactions, must preserve the consistency of the database.
3. isolation: Users should be able to understand a transaction without considering the effect of other concurrently executing transactions, even if the DBJVIS interleaves the actions of transactions for performance reasons.
4. durability: Once the DBMS informs the user that a transaction been successfully completed, its effects should persist even if the system crashes before all its changes are reflected on disk.

### Anomalies

#### WR Conflicts

a transaction T2 could read a database object A that has been modified by another transaction Tl, which has not yet committed.

Such a read is called a dirty read.

#### RW Conflicts

T2 could change the value of an object A that has been read by a transaction Tl, while Tl is still in progress.

#### WW Conflicts

a transaction T2 could overwrite the value of an object A, which has already been modified by a transaction Tl,while Tl is still in progress.



### Lock-Based Concurrency Control

A locking protocol is a set rules to be followed by each transaction (and enforced by the DBMS) to ensure that, even though actions of several transactions might be interleaved, net effect is identical to all transactions in some serial order.

#### S2PL

rules：

1. If a transaction T wants to read modify) an object, it first a shared (respectively, exclusive) lock on the object.
2. All locks held by a transaction are released when the transaction is completed .

May cause deadlock

Performance may be caused by blocking and aborting

the overhead locking comes primarily from delays due to blocking. 

![image-2022123070222236 PM](https://i.imgur.com/NOrLU3h.png)

并发数高的时候会有抖动

减少抖动方法：

* By locking the smallest sized objects possible (reducing the likelihood that two transactions need the same lock).
* By reducing tiIne that transaction hold locks (so that other transactions are blocked for a shorter time).
* By reducing hot spots. A hot spot is a database object that is frequently accessed and modified, and causes a lot of blocking delays. Hot spots can significantly affect perfonnance.

### Stealing and Forcing Pages

Steal : Can the changes made to an object 0 in the buffer pool by a transaction T be written to disk before T commits? Such writes are executed when another transaction wants to bring in a page and the buffer nlanager chooses to replace the frame containing 0; of course, this page must have been unpinned by T. If such writes are allowed, we say that a steal approach is used. (Informally, the second transaction 'steals' a frame from T.)

Force: When a transaction commits, must we ensure that all the changes it has made to objects in the buffer pool are immediately forced to disk? If so, we say that: a force approach is used.

If a no-steal approach is used, we do not have to undo the changes of an aborted transaction (because these changes have not been written to disk)

if a force approach is used, we do not have to redo the changes of a committed transaction if there is a subsequent crash (because all these changes are guaranteed to have been written to disk at commit time).

# Concurrency Control

### 2PL ,serializability, recoverability

##### conflict equivalent:

 if they involve the (same set of) actions of the same transactions and they order every pair of conflicting actions of two committed transactions in the same way.

相同事物的相同操作，且每一对冲突的操作在两个调度中顺序相同（WW,WR,RR)

##### Conflict action:

 Two actions of different transactions are conflicting if they access the same object and one of them is a write

##### conflict serializable: 

it is conflict equivalent to some serial schedule.

##### precedence graph(serializability graph): 

* 点是committed transaction
* 边是有confilect 的pair，先发生的指向后发生的，并且与commit无关

##### 2PL

relaxes the second rule of Strict 2PL to allow transactions to release locks before the end.

用的时候申请，用完了就释放且无法再次申请

##### view equivalent

1. 对同一 data item， 只要是有一个 schedule读了它的初始值，另外一个 schedule 也必须读它的初始值。

2. 对同一data  item,如果在一个 schedule 里，一个操作是读了一个写操作后的值，另一个 schedule 也必须读同样写操作后的值。

3. 对同一 data item,如果在一个 schedule 里最后进行了写操作，则另一个 schedule 也要在最后进行同样的写操作。


##### DeadLock

lock manager maintains a structure called a waits-for graph to detect deadlock cycles.

Nodes: active transactions

Arc： from $ T_i  $ to  $T_j$ if an only if $T_i$ is waiting for $T_j$ to release a lock

如果i和j都commit了就加一条反方向的线

Waits-for graph 周期性检查有没有循环，有的话就abort某一个transaction并且释放他的锁。

There is an edge from Ti to Tj if Ti is waiting for Tj to release a lock

![image-20230112115706850 PM](https://i.imgur.com/PgpE4eC.png)

##### Deadlock Prevention

We can prevent deadlocks by giving each transaction a priority and ensuring that lower-priority transactions are not allowed to wait higher-priority transactions (or vice versa).反之亦然

如果a要一个锁，b拿着这个锁，就看优先级，有两种策略：

Wait-die: If Ti has higher priority, it is allowed to wait; otherwise, it is aborted.

Wound-wait: If Ti has higher priority, abort Tj; otherwise, Ti waits.

第一个里低优先级永远不等高优先级，第二个里高优先级永远不等低优先级

##### Deadlock Detection

画图看有没有环

##### Optimistic Concurrency Control

Read,Validation,Write



## Recovery

每条log都有prevLSN,transID,type(updating,commit,abort,end,undoing).

Transaction Table 包含所有active action，有transaction id, status, lastLSN

Dirty page Table 包含buffer pool的 page， 有recLSN(第一个让这个page进入的记录)和pageID

遇到end再删transaction table中的记录

winner and loser transaction: winner是commit过的transaction，loser是没能commit的，they were still active while the crash occured.

Redo starts at the smallest recLSN in the dirty page,and all logs related to pages in the dirty page need to be redone.

Undo follow redo and ends all loser set transaction. Undo find the lastLSN of transactions in the loser set and undo all its work.

![image-2023012552829664 PM](../../Library/Application Support/typora-user-images/image-2023012552829664 PM.png)

第一个是dpt里最小的，第二个是loser里的最早的。

![image-2023012542634769 PM](https://i.imgur.com/Z238DOQ.png)

第一个是表里有pageID且在dirty page table里有的记录需要redo。

第二个是loser集合里的所有transaction的LSN。

Redo从上往下， Undo从下往上。

after log: 

* 先end所有commit的transaction
* 先abort当前inprogress的transaction
* 上两个的PREV_LSN都是上一步相关的LSN
* 然后undo这些transaction的log。第一条CLR的PREV_LSN写abort的LSN，后续PREV_LSN都写上一个CLR的LSN。CLR还有undonextLSN字段，CLR写法：CLR:Undo T1 LSN 9 (undonextLSN 4)，CLR的pageID和这条log初始的ID一样，需要写。
* 然后end这个transaction
* 顺序与上边update顺序相同

若遇到CLR过程中crash就继续上边没完成的CLR之类，有end就先end

# 题型

## 求Vector Clock顺序

从(0,0,0)开始，每当接收到用户发来的更新消息时就当前对应数字+1之后用新数据发消息。假如接收到的数据和当前的有新的也有旧的就需要call an application-specfic merge function to combine both values consistently,不然的话就overwrite value.

## 画Waits-for graph判断是否有死锁

首先给每个Action都标上共享锁（SA）或者排它锁（XA），然后当有T1等T2的时候就画一条T1指向T2的线。如果最终图里有环则有可能会发生死锁

## 画precedence graph判断是否能转换成conflict-serializable

点是所有transaction，边是action之间的冲突并且先执行的指向后执行的。冲突是两个transaction都对同一个对象进行操作并且有一个是W

有环就不是conflict-serializable

## 哪种TPL会有死锁？为什么其他的不会有死锁？

S2PL和2PL会有（所有逐步获得锁的都会有）

其他的都一次性拿到所有锁，所以不会死锁。

## 判断一个schedule能不能被s2pl产生

presendence graph有环就不能被所有产生

首先schedule必须是conflict-serializable

其次只要有要锁的过程就不能产生，先inject S/X lock,然后判断

## 判断一个schedule能不能被2pl产生

逐步拿锁，后边不需要的时候就放，但是一旦开始放锁就不能再要锁

## 判断一个schedule能不能被c2pl产生

一个transaction一次拿全所有的锁，用完就放。只需要判断其他t有没有要他还没放的锁

## Optimisic Concurrency Control (Kung-Robinson)

![image-2023012515132027 PM](https://i.imgur.com/Ty6vqiQ.png)

![image-2023012515139272 PM](https://i.imgur.com/SCCwAXA.png)

不为空的话会引起read dirty data

![image-2023012515151003 PM](https://i.imgur.com/Is0lLM7.png)

不为空的话会引起read dirty data 和 overwrite write

## 写一个conflict-serializable但不是S2PL的schedule



## 写一个是S2PL但不是C2PL的schedule

S2PL用完就放锁，C2PL用之前全要完锁

## 判断serializable

serializable：不管T123先后顺序怎么变，最后对数据库结果一样

View-serializable: 两个schedule看起来效果是一样的，在A中某位置的读在B中会同样读到一样的数据

conflict-serializable： 画图没环

## force和steal

As steal is used, we need to implement undo because if some data being written to the disk because of steal of RAM and there’s a crush, we will need to undo it. As force is used, every update are written into the disk instead of RAM so we don’t need to Redo.

