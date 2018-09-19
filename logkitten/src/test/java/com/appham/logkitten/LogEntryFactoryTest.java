package com.appham.logkitten;

import com.appham.logkitten.service.LogEntry;
import com.appham.logkitten.service.LogEntryFactory;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class LogEntryFactoryTest {

    @Test
    public void testBuildLogEntryEmpty() {
        String logline = "";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        assertEquals("", logEntry.getTime());
        assertEquals("", logEntry.getPid());
        assertEquals("", logEntry.getLevel());
        assertEquals("", logEntry.getContent());
    }

    @Test
    public void testBuildLogEntryWarningValid() {
        String logline = "08-29 20:23:46.390  3397  3397 W System.err: java.lang.RuntimeException: Test Exception Demo";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        assertEquals("08-29 20:23:46.390", logEntry.getTime());
        assertEquals("3397  3397", logEntry.getPid());
        assertEquals("W", logEntry.getLevel());
        assertEquals("System.err: java.lang.RuntimeException: Test Exception Demo", logEntry.getContent());
    }

    @Test
    public void testBuildLogEntryErrorValid() {
        String logline = "08-29 20:23:51.574  3397  3397 E AndroidRuntime: java.lang.IllegalStateException: Could not execute method for android:onClick";
        LogEntry logEntry = LogEntryFactory.buildLogEntry(logline);

        assertEquals("08-29 20:23:51.574", logEntry.getTime());
        assertEquals("3397  3397", logEntry.getPid());
        assertEquals("E", logEntry.getLevel());
        assertEquals("AndroidRuntime: java.lang.IllegalStateException: Could not execute method for android:onClick", logEntry.getContent());
    }

    @Test
    public void testFindUrlHttp() {
        assertEquals("http://www.github.com/donfuxx/LogKitten",
                LogEntryFactory.findUrl("test some url http://www.github.com/donfuxx/LogKitten here").toString());
    }

    @Test
    public void testFindUrlHttps() {
        assertEquals("https://www.github.com/donfuxx/LogKitten",
                LogEntryFactory.findUrl("test some url https://www.github.com/donfuxx/LogKitten here").toString());
    }

    @Test
    public void testFindUrlNoHttp() {
        assertEquals(null,
                LogEntryFactory.findUrl("test some url www.github.com/donfuxx/LogKitten here"));
    }

    @Test
    public void testFindUrlNone() {
        assertEquals(null,
                LogEntryFactory.findUrl("no url here"));
    }

    @Test
    public void testFindUrlPackage() {
        assertEquals(null,
                LogEntryFactory.findUrl("package name here com.appham.android only"));
    }

    @Test
    public void testFindUrlNoProtocol() {
        assertEquals(null,
                LogEntryFactory.findUrl("no protocol ApiCallback.onFailure"));
    }
}