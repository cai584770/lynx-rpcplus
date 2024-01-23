package org.grapheco.lynx.cypherbio.sccg.entity;

public class Position {
    public int startinRef;
    public int endinRef;
    private int startinTar;
    private int endinTar;

    public Position() {

    }

    public Position(int startinRef, int endinRef, int startinTar, int endinTar) {
        this.startinRef = startinRef;
        this.endinRef = endinRef;
        this.startinTar = startinTar;
        this.endinTar = endinTar;
    }

    public int getstartinRef() {
        return startinRef;
    }

    public void setstartinRef(int startinRef) {
        this.startinRef = startinRef;
    }

    public int getendinRef() {
        return endinRef;
    }

    public void setendinRef(int endinRef) {
        this.endinRef = endinRef;
    }

    public int getstartinTar() {
        return startinTar;
    }

    public void setstartinTar(int startinTar) {
        this.startinTar = startinTar;
    }

    public int getendinTar() {
        return endinTar;
    }

    public void setendinTar(int endinTar) {
        this.endinTar = endinTar;
    }

    @Override
    public String toString() {
        String str = "startinRef:" + Integer.toString(startinRef) + ",endinRef:" + Integer.toString(endinRef)
                + ",startinTar:"
                + Integer.toString(startinTar) + ",endinTar:" + Integer.toString(endinTar);
        return str;
    }
}
