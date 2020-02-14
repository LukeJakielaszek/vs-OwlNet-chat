package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message_A> {

    @Override
    public int compare(Message_A lhs, Message_A rhs) {
        // Write your code here
        if(lhs.getTimestamp().happenedBefore(rhs.getTimestamp())){
            return -1;
        }else {
            return 1;
        }
    }
}
