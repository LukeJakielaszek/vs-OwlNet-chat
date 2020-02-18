package edu.temple.vs_owlnet_chat;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.inf.vs.a3.clock.Clock;

public class VectorClock implements Clock {
    // vectorclock map implementation
    private Map<Integer, Integer> vector;

    // return vector map
    public Map<Integer, Integer> getClock(){
        return this.vector;
    }

    // initialize to empty map
    public VectorClock(){
        this.vector = new HashMap<>();
    }

    // return the time associated with a single process
    public int getTime(Integer pid){
        return this.vector.get(pid);
    }

    // add a process to the vector clock with a specified time
    public void addProcess(Integer pid, int time){
        this.vector.put(pid, time);
    }

    // updates all indices of vectorclock to the largest of the two clocks
    @Override
    public void update(Clock other) {
        // convert to vectorclock
        Map<Integer, Integer> otherClock = ((VectorClock)other).getClock();

        // loop through other clocks keyset
        for(Integer key : otherClock.keySet()){
            // get the val associated with the otherclock
            Integer curVal = otherClock.get(key);

            // if our map also has the key, compare values
            if(this.vector.containsKey(key)){
                // update the pid value if our associated time is less than the other clock's time
                if(this.vector.get(key) < curVal) {
                    this.vector.put(key, curVal);
                }
            }else{
                // set the val directly if current map doesnt have key
                this.vector.put(key, curVal);
            }
        }
    }

    // set the current clock to the other by value
    @Override
    public void setClock(Clock other) {
        // clear our current clock
        this.vector.clear();

        // cast the other to a vectorclock
        VectorClock vectorClock = (VectorClock)other;

        // copy all key values from other clock to ours
        for(Integer key: vectorClock.getClock().keySet()){
            this.vector.put(key, vectorClock.getTime(key));
        }
    }

    // increment the logical time for indicated process
    @Override
    public void tick(Integer pid) {
        if(this.vector.containsKey(pid)){
            // if we have the pid tracked, increment by 1
            this.vector.put(pid, this.vector.get(pid) + 1);
        }else{
            // if key DNE, add it and init to 1
            this.vector.put(pid, 1);
        }
    }

    // return true if our clock happens before the other clock
    @Override
    public boolean happenedBefore(Clock other) {
        // cast other to vectorclock
        VectorClock vectorClock = (VectorClock)other;

        // get the map of the otherclock
        Map<Integer, Integer> clock = vectorClock.getClock();
        // loop through each key of the other clock
        for(Integer key : clock.keySet()){
            // if we do not have the key, skip
            if(!this.vector.containsKey(key)){
                continue;
            }else if(this.vector.get(key) > clock.get(key)){
                // if we have the key and its val is greater, return false
                return false;
            }
        }

        // loop through our keyset and ensure all keys are in the other clock
        for(Integer key : this.vector.keySet()){
            if(!clock.containsKey(key)){
                // if other clock does not have all of our keys, we happened after
                return false;
            }
        }

        // indicate that our clock precedes other
        return true;
    }

    // converts a vector clock to a string representation
    @NonNull
    @Override
    public String toString() {
        // initialize stringbuilder object
        StringBuilder stringBuilder = new StringBuilder();

        // all clocks must start with { character
        stringBuilder.append("{");

        // loop through all keys in our current clock
        int i = 0;
        for(Integer key : this.vector.keySet()){
            // track number of keys
            i++;

            // append key
            stringBuilder.append("\"");
            stringBuilder.append(key);
            stringBuilder.append("\":");

            // append value
            stringBuilder.append(this.vector.get(key));

            // if not last key, add a comma for next key/value pair
            if(i != this.vector.keySet().size()) {
                stringBuilder.append(",");
            }

        }

        // all clocks end with a } character
        stringBuilder.append("}");

        // return the string representation of the clock
        return stringBuilder.toString();
    }

    // sets this vectorclock from a string
    @Override
    public void setClockFromString(String clock) {
        // if string is null, do nothing
        if(clock == null){
            return;
        }

        // check if clock is currently empty
        if(clock.equals("{}")) {
            // initialize to empty hashmap
            this.vector = new HashMap<>();
        }else {
            // attempt to parse non-empty clock
            try {
                // convert string to a stringbuilder object for parsing
                StringBuilder stringBuilder = new StringBuilder(clock);

                // remove {} characters
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                // create a map to hold key value pairs
                HashMap<Integer, Integer> newClock = new HashMap<>();

                // split by key value pair
                for (String set : stringBuilder.toString().split(",")) {
                    int count = 0;
                    Integer key = -1;
                    Integer val = -1;

                    // split keys & values
                    for(String mapping : set.split(":")){
                        if(count == 0){
                            // parse key by removing \ characters
                            key = Integer.parseInt(mapping.substring(1, mapping.length() - 1));
                        }else {
                            // parse values
                            val = Integer.parseInt(mapping);
                        }

                        // increment current count of key value section
                        count++;
                    }

                    // if we failed to set key or value, indicate failure
                    if(key == -1 || val == -1){
                        throw new Exception();
                    }

                    // store the key value pair
                    newClock.put(key, val);
                }

                // update clock if string successfully parsed
                this.vector = newClock;

            }catch (Exception e){
                // invalid clock format. Do nothing
            }
        }
    }
}
