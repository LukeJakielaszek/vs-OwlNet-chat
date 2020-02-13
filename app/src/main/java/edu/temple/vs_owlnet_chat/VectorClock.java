package edu.temple.vs_owlnet_chat;

import java.util.Map;

import ch.ethz.inf.vs.a3.clock.Clock;

public class VectorClock implements Clock {
    private Map<Integer, Integer> vector;

    public int getTime(Integer pid){
        return 0;
    }

    public void addProcess(Integer pid, int time){

    }

    @Override
    public void update(Clock other) {

    }

    @Override
    public void setClock(Clock other) {

    }

    @Override
    public void tick(Integer pid) {

    }

    @Override
    public boolean happenedBefore(Clock other) {
        return false;
    }

    @Override
    public void setClockFromString(String clock) {

    }
}
