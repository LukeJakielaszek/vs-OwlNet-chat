package edu.temple.vs_owlnet_chat;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.inf.vs.a3.clock.Clock;

public class VectorClock implements Clock {
    private Map<Integer, Integer> vector;

    public Map<Integer, Integer> getClock(){
        return this.vector;
    }

    public VectorClock(){
        this.vector = new HashMap<>();
    }

    public int getTime(Integer pid){
        return this.vector.get(pid);
    }

    public void addProcess(Integer pid, int time){
        this.vector.put(pid, time);
    }

    @Override
    public void update(Clock other) {
        Map<Integer, Integer> otherClock = ((VectorClock)other).getClock();

        for(Integer key : otherClock.keySet()){
            Integer curVal = otherClock.get(key);
            if(this.vector.containsKey(key)){
                if(this.vector.get(key) < curVal) {
                    this.vector.put(key, curVal);
                }
            }else{
                this.vector.put(key, curVal);
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        this.vector.clear();

        VectorClock vectorClock = (VectorClock)other;

        for(Integer key: vectorClock.getClock().keySet()){
            this.vector.put(key, vectorClock.getTime(key));
        }
    }

    @Override
    public void tick(Integer pid) {
        if(this.vector.containsKey(pid)){
            this.vector.put(pid, this.vector.get(pid) + 1);
        }else{
            this.vector.put(pid, 1);
        }
    }

    @Override
    public boolean happenedBefore(Clock other) {
        VectorClock vectorClock = (VectorClock)other;

        Map<Integer, Integer> clock = vectorClock.getClock();
        for(Integer key : clock.keySet()){
            if(!this.vector.containsKey(key)){
                return false;
            }else if(this.vector.get(key) > clock.get(key)){
                return false;
            }
        }

        return true;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        int i = 0;
        for(Integer key : this.vector.keySet()){
            i++;

            stringBuilder.append("\"");
            stringBuilder.append(key);
            stringBuilder.append("\":");
            stringBuilder.append(this.vector.get(key));

            if(i != this.vector.keySet().size()) {
                stringBuilder.append(",");
            }

        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public void setClockFromString(String clock) {
        if(clock.equals("{}")) {
            this.vector = new HashMap<>();
        }else {
            try {
                StringBuilder stringBuilder = new StringBuilder(clock);

                // remove {} characters
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                HashMap<Integer, Integer> newClock = new HashMap<>();

                for (String set : stringBuilder.toString().split(",")) {
                    int count = 0;
                    Integer key = -1;
                    Integer val = -1;
                    for(String mapping : set.split(":")){
                        if(count == 0){
                            key = Integer.parseInt(mapping.substring(1, mapping.length() - 1));
                        }else {
                            val = Integer.parseInt(mapping);
                        }
                        count++;
                    }

                    if(key == -1 || val == -1){
                        throw new Exception();
                    }

                    newClock.put(key, val);
                }

                this.setClock((Clock)newClock);

            }catch (Exception e){
                // invalid clock format. Do nothing
            }
        }
    }
}
