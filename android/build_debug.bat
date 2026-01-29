@echo off
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
call gradlew.bat compileDebugKotlin --no-daemon
pause
