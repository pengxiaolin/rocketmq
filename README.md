# rocketmq
rocketmq 实例

rocket安装与配置
下载编译好的bin，不用下载源码版本 
1. wget http://mirror.bit.edu.cn/apache/rocketmq/4.2.0/rocketmq-all-4.2.0-bin-release.zip 
2. 解压
3. 进入bin目录，根据实际情况修改 runserver.sh、runbroker.sh的配置参数
4. 启动namesrv服务：nohup sh mqnamesrv > vienout.txt 2>&1 &
5. 启动broker服务：nohup sh mqbroker -n 172.18.84.207:9876 autoCreateTopicEnable=true -c /home/rocketmq/conf/2m-2s-async/broker-a.properties > vienout2.txt 2>&1 &
6. 关闭namesrv服务：./mqshutdown namesrv
7. 关闭broker服务 ：./mqshutdown broker 
8. 需指定broker服务的路由服务的地址 在具体的配置文件中添加参数
 namesrvAddr=172.18.84.207:9876

 参考常用命令 http://m635674608.iteye.com/blog/2299240
