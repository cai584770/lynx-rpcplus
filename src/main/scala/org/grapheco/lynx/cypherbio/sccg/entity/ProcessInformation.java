package org.grapheco.lynx.cypherbio.sccg.entity;

public class ProcessInformation {
    private String targetSequence;
    private String Nlist;
    private String Llist;

    public ProcessInformation(String targetSequence, String Nlist, String Llist) {
        this.targetSequence = targetSequence;
        this.Nlist = Nlist;
        this.Llist = Llist;
    }

    public String getTargetSequence() {
        return targetSequence;
    }

    public String getNlist() {
        return Nlist;
    }

    public String getLlist() {
        return Llist;
    }

    @Override
    public String toString() {
        String resuString = Llist.toString() + "\n" + Nlist.toString() + "\n";

        return resuString;
    }
}
