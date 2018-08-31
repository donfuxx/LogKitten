# LogKitten

LogKitten is an **Android library module** that can be added to your Android project for example as a `debugImplementation` to make inspecting the logcat on the device easier in the following ways:
 - The LogKitten Service will be **started by clicking the "LogKitten" launcher icon** and it will monitor your app's logcat for Error and Warning level logs and will show them as notifications.
 ![logkitten_notification_screenshot](https://raw.githubusercontent.com/donfuxx/LogKitten/a25dbccee9c9445f78363994b2f62fba79951633/media/logkitten_notification_screenshot.png)
 - Logs can be shared by clicking the **share action** in a LogKitten notification. This is a handy tool if you have Testers for your project that want to forward crash logcats to the developers or a bugtracking system like Jira etc.
 - LogKitten service can be **stopped anytime**, by clicking the "Stop" action in the Service notification.
 - LogKitten service can be **restarted anytime** by clicking the "LogKitten" launcher icon.
 - It is recommended to include LogKitten **only in debug builds**, but not in your production release build. LogKitten is usefull for developers and testers, but pretty useless for real users of you app.

![logkitten_ic_launcher-web](https://user-images.githubusercontent.com/8261416/44672957-04be7200-aa22-11e8-987d-bdc8c51d3f29.png)

## Setup Guide

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

    	allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}

### Step 2. Add the debug dependency
Add it in your app module gradle dependencies. It is recommended to use `debugImplementation`

    	dependencies {
    	        debugImplementation 'com.github.donfuxx:LogKitten:v1.1.1'
    	}
