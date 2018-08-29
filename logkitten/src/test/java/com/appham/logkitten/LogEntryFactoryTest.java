package com.appham.logkitten;

import junit.framework.Assert;

import org.junit.Test;

public class LogEntryFactoryTest {

    @Test
    public void testBuildLogEntryEmpty() {
        String logline = "";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        Assert.assertEquals("", logEntry.getTime());
        Assert.assertEquals("", logEntry.getLevel());
        Assert.assertEquals("", logEntry.getPid());
        Assert.assertEquals("", logEntry.getContent());
    }
}