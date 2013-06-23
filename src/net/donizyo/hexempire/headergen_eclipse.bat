@echo off
cd ..
cd ..
cd ..
cd hexempire
cd hexempireserver
cd bin

echo 正在生成头文件...

%JAVA_HOME%\bin\javah net.donizyo.hexempire.ServerEntry

echo 头文件已成功生成.