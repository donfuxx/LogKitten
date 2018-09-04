package com.appham.logkitten;

import com.appham.logkitten.service.LogEntry;
import com.appham.logkitten.service.LogEntryFactory;

import junit.framework.Assert;

import org.junit.Test;

public class LogEntryFactoryTest {

    @Test
    public void testBuildLogEntryEmpty() {
        String logline = "";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        Assert.assertEquals("", logEntry.getTime());
        Assert.assertEquals("", logEntry.getPid());
        Assert.assertEquals("", logEntry.getLevel());
        Assert.assertEquals("", logEntry.getContent());
    }

    @Test
    public void testBuildLogEntryWarningValid() {
        String logline = "08-29 20:23:46.390  3397  3397 W System.err: java.lang.RuntimeException: Test Exception Demo";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        Assert.assertEquals("08-29 20:23:46.390", logEntry.getTime());
        Assert.assertEquals("3397  3397", logEntry.getPid());
        Assert.assertEquals("W", logEntry.getLevel());
        Assert.assertEquals("System.err: java.lang.RuntimeException: Test Exception Demo", logEntry.getContent());
    }

    @Test
    public void testBuildLogEntryErrorValid() {
        String logline = "08-29 20:23:51.574  3397  3397 E AndroidRuntime: java.lang.IllegalStateException: Could not execute method for android:onClick";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        Assert.assertEquals("08-29 20:23:51.574", logEntry.getTime());
        Assert.assertEquals("3397  3397", logEntry.getPid());
        Assert.assertEquals("E", logEntry.getLevel());
        Assert.assertEquals("AndroidRuntime: java.lang.IllegalStateException: Could not execute method for android:onClick", logEntry.getContent());
    }
}