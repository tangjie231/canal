zookeeper配置
1：下载zookeeper并解压
2：配置zoo.cfg
   cp zoo_simple.cfg zoo.cfg
   配置如下：

   # The number of milliseconds of each tick
   tickTime=2000
   # The number of ticks that the initial
   # synchronization phase can take
   initLimit=10
   # The number of ticks that can pass between
   # sending a request and getting an acknowledgement
   syncLimit=5
   # the directory where the snapshot is stored.
   # do not use /tmp for storage, /tmp here is just
   # example sakes.
   dataDir=/root/zk/data
   # the port at which the clients will connect
   clientPort=2181
   # the maximum number of client connections.
   # increase this if you need to handle more clients
   #maxClientCnxns=60
   #
   # Be sure to read the maintenance section of the
   # administrator guide before turning on autopurge.
   #
   # http://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance
   #
   # The number of snapshots to retain in dataDir
   #autopurge.snapRetainCount=3
   # Purge task interval in hours
   # Set to "0" to disable auto purge feature
   #autopurge.purgeInterval=1
3：启动zookeeper
   bin/zkServer.sh start

4:配置canal instance.properties:


  #################################################
  ## mysql serverId
  canal.instance.mysql.slaveId = 3

  # position info
  canal.instance.master.address = 192.168.0.14:3306
  canal.instance.master.journal.name =
  canal.instance.master.position =
  canal.instance.master.timestamp =

  #canal.instance.standby.address =
  #canal.instance.standby.journal.name =
  #canal.instance.standby.position =
  #canal.instance.standby.timestamp =

  # username/password
  canal.instance.dbUsername = canal
  canal.instance.dbPassword = canal
  canal.instance.defaultDatabaseName = test
  canal.instance.connectionCharset = UTF-8

  # table regex
  canal.instance.filter.regex = .*\\..*
  # table black regex
  canal.instance.filter.black.regex =

  #################################################

5:配置canal.properties:
  #################################################
  ######### 		common argument		#############
  #################################################
  canal.id= 1
  canal.ip=
  canal.port= 11111
  canal.zkServers= 192.168.0.14:2181
  # flush data to zk
  canal.zookeeper.flush.period = 1000
  # flush meta cursor/parse position to file
  canal.file.data.dir = ${canal.conf.dir}
  canal.file.flush.period = 1000
  ## memory store RingBuffer size, should be Math.pow(2,n)
  canal.instance.memory.buffer.size = 16384
  ## memory store RingBuffer used memory unit size , default 1kb
  canal.instance.memory.buffer.memunit = 1024
  ## meory store gets mode used MEMSIZE or ITEMSIZE
  canal.instance.memory.batch.mode = MEMSIZE

  ## detecing config
  canal.instance.detecting.enable = false
  #canal.instance.detecting.sql = insert into retl.xdual values(1,now()) on duplicate key update x=now()
  canal.instance.detecting.sql = select 1
  canal.instance.detecting.interval.time = 3
  canal.instance.detecting.retry.threshold = 3
  canal.instance.detecting.heartbeatHaEnable = false

  # support maximum transaction size, more than the size of the transaction will be cut into multiple transactions delivery
  canal.instance.transaction.size =  1024
  # mysql fallback connected to new master should fallback times
  canal.instance.fallbackIntervalInSeconds = 60

  # network config
  canal.instance.network.receiveBufferSize = 16384
  canal.instance.network.sendBufferSize = 16384
  canal.instance.network.soTimeout = 30

  # binlog filter config
  canal.instance.filter.query.dcl = false
  canal.instance.filter.query.dml = false
  canal.instance.filter.query.ddl = false
  canal.instance.filter.table.error = false

  # binlog format/image check
  canal.instance.binlog.format = ROW,STATEMENT,MIXED
  canal.instance.binlog.image = FULL,MINIMAL,NOBLOB

  # binlog ddl isolation
  canal.instance.get.ddl.isolation = false

  #################################################
  ######### 		destinations		#############
  #################################################
  canal.destinations= my
  # conf root dir
  canal.conf.dir = ../conf
  # auto scan instance dir add/remove and start/stop instance
  canal.auto.scan = true
  canal.auto.scan.interval = 5

  canal.instance.global.mode = spring
  canal.instance.global.lazy = false
  #canal.instance.global.manager.address = 127.0.0.1:1099
  #canal.instance.global.spring.xml = classpath:spring/memory-instance.xml
  canal.instance.global.spring.xml = classpath:spring/file-instance.xml
  #canal.instance.global.spring.xml = classpath:spring/default-instance.xml
  ###########################################################################
6:启动canal
  sh bin/startup.sh

7:运行client程序

8：再复制一个canal组成集群形式
  canal.properties中修改，保证和另外一个canal不一样即可，其他配置都一样
   canal.id= 2
   canal.port= 11112
9：zkCli使用
   [zk: 192.168.0.14:2181(CONNECTED) 21] ls /
   [otter, zookeeper]
   [zk: 192.168.0.14:2181(CONNECTED) 22] ls /otter
   [canal]
   [zk: 192.168.0.14:2181(CONNECTED) 23] ls /otter/canal
   [cluster, destinations]
   [zk: 192.168.0.14:2181(CONNECTED) 24] ls /otter/canal/cluster
   [192.168.0.14:11111, 192.168.0.14:11112]
   [zk: 192.168.0.14:2181(CONNECTED) 25] ls /otter/canal/destinations
   [example, my]
   [zk: 192.168.0.14:2181(CONNECTED) 26] ls /otter/canal/destinations/example
   [cluster, running]
   [zk: 192.168.0.14:2181(CONNECTED) 27] ls /otter/canal/destinations/example/running
   []
   [zk: 192.168.0.14:2181(CONNECTED) 28] ls /otter/canal/destinations/my/running
   []
   [zk: 192.168.0.14:2181(CONNECTED) 29] get /otter/canal/destinations/my/running
   {"active":true,"address":"192.168.0.14:11112","cid":2}
   cZxid = 0x50
   ctime = Sun Sep 20 06:05:25 EDT 2015
   mZxid = 0x50
   mtime = Sun Sep 20 06:05:25 EDT 2015
   pZxid = 0x50
   cversion = 0
   dataVersion = 0
   aclVersion = 0
   ephemeralOwner = 0x14fe9f9f302000a
   dataLength = 54
   numChildren = 0
   [zk: 192.168.0.14:2181(CONNECTED) 30] get /otter/canal/destinations/example/running
   {"active":true,"address":"192.168.0.14:11112","cid":2}
   cZxid = 0x51
   ctime = Sun Sep 20 06:05:25 EDT 2015
   mZxid = 0x51
   mtime = Sun Sep 20 06:05:25 EDT 2015
   pZxid = 0x51
   cversion = 0
   dataVersion = 0
   aclVersion = 0
   ephemeralOwner = 0x14fe9f9f302000a
   dataLength = 54
   numChildren = 0





