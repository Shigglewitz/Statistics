package org.dkeeney.git.commit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Commit {
    private final List<Module> modules;
    private final String hash;
    private String author;
    private String merge;
    private Date created;
    private String comments;

    public Commit(String hash) {
        this.hash = hash;
        this.modules = new ArrayList<Module>();
        this.comments = "";
    }

    public void addModule(Module modules) {
        this.modules.add(modules);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(this.modules);
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMerge() {
        return this.merge;
    }

    public void setMerge(String merge) {
        this.merge = merge;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getComments() {
        return this.comments;
    }

    public void addCommentLine(String commentLine) {
        this.comments += commentLine + System.lineSeparator();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getHash() {
        return this.hash;
    }
}
