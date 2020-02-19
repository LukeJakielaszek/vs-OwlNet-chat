package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message_A> {

    @Override
    public int compare(Message_A lhs, Message_A rhs) {
        // returns -1 if lhs has all array indices less than rhs, otherwise 1
        if(lhs.getTimestamp().happenedBefore(rhs.getTimestamp())){
            return -1;
        }else if(rhs.getTimestamp().happenedBefore(lhs.getTimestamp())) {
            return 1;
        } else{
            // if both false, timestamps are in conflict
            return 0;
        }
    }
}
