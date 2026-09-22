@echo off
setlocal
if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.4.7-hotspot"
"C:\smart-society-module-main\maven\apache-maven-3.9.16\bin\mvn.cmd" %*
