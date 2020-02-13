package edu.temple.vs_owlnet_chat;

import androidx.annotation.NonNull;

import ch.ethz.inf.vs.a3.clock.Clock;

public class LamportClock implements Clock {

    private int time;

    public LamportClock(){
        this.time = 0;
    }

    public void setTime(int time){
        this.time = time;

    }

    public int getTime(){
        return this.time;
    }

    @Override
    public void update(Clock other) {
        LamportClock otherClock = (LamportClock)other;

        if(otherClock.getTime() > this.time){
            this.time = otherClock.getTime();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return new Integer(this.time).toString();
    }

    @Override
    public void setClock(Clock other) {
        LamportClock otherClock = (LamportClock)other;
        this.setTime(otherClock.getTime());
    }

    @Override
    public void tick(Integer pid) {
        this.time++;

    }

    @Override
    public boolean happenedBefore(Clock other) {
        LamportClock otherClock = (LamportClock)other;

        if(otherClock.getTime() <= this.time){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void setClockFromString(String clock) {
        boolean isTime = true;
        for(int c : clock.toCharArray()){
            if(c < 48 || c > 57){
                isTime = false;
            }
        }
        if(isTime && !clock.isEmpty()){
            this.time = Integer.parseInt(clock);
        }
    }
}

