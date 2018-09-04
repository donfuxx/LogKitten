package com.appham.logkitten.service;

public class LogEntry {
    private String time;
    private String pid;
    private String level;
    private String content;

    public LogEntry() {
        //empty
    }

    public LogEntry(String time, String pid, String level, String content) {
        this.setTime(time);
        this.setPid(pid);
        this.setLevel(level);
        this.setContent(content);
    }

    public String getTitle() {
        return time + " - " + pid + " - " + level;
    }

    public String getPidLevel() {
        return getPid() + " - " + getLevel();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
