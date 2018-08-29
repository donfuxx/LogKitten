package com.appham.logkitten;

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
}