package org.grapheco.lynx.cypherbio.sccg.entity;

public class NewKmer {
    private String kmer;
    private int kmerstart;

    public String getkmer() {
        return kmer;
    }

    public void setkmer(String kmer) {
        this.kmer = kmer;
    }

    public int getkmerstart() {
        return kmerstart;
    }

    public void setkmerstart(int kmerstart) {
        this.kmerstart = kmerstart;
    }
}
