package edu.temple.vs_owlnet_chat;

import androidx.annotation.NonNull;

import ch.ethz.inf.vs.a3.clock.Clock;

public class LamportClock implements Clock {

    // track logical lamport time
    private int time;

    // init time to zero
    public LamportClock(){
        this.time = 0;
    }

    // overwrite time to the set time
    public void setTime(int time){
        this.time = time;

    }

    // return the current time
    public int getTime(){
        return this.time;
    }

    // update the current time to the largest of the two times
    @Override
    public void update(Clock other) {
        LamportClock otherClock = (LamportClock)other;

        if(otherClock.getTime() > this.time){
            this.time = otherClock.getTime();
        }
    }

    // convert the time to a string
    @NonNull
    @Override
    public String toString() {
        return new Integer(this.time).toString();
    }

    // set the time from other clock to this clock
    @Override
    public void setClock(Clock other) {
        LamportClock otherClock = (LamportClock)other;
        this.setTime(otherClock.getTime());
    }

    // increment the time by 1, ignore pid
    @Override
    public void tick(Integer pid) {
        this.time++;

    }

    // returns true if this time is strictly less than the other clock
    @Override
    public boolean happenedBefore(Clock other) {
        // cast the clock to lamport
        LamportClock otherClock = (LamportClock)other;

        // compare times
        if(otherClock.getTime() <= this.time){
            return false;
        }else {
            return true;
        }
    }

    // converts a string to a lamport clock
    @Override
    public void setClockFromString(String clock) {
        // ensure the string contains an integer
        boolean isTime = true;
        for(int c : clock.toCharArray()){
            if(c < 48 || c > 57){
                isTime = false;
            }
        }

        // set the time to the clock if no errors detected
        if(isTime && !clock.isEmpty()){
            this.time = Integer.parseInt(clock);
        }
    }
}

