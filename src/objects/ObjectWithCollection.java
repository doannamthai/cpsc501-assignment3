package objects;

import java.util.LinkedList;
import java.util.Queue;

public class ObjectWithCollection implements SupportObject{

    public Queue<SupportObject> queue = new LinkedList<>();

    public Queue<SupportObject> getQueue() {
        return queue;
    }

    public void setQueue(Queue<SupportObject> queue) {
        this.queue = queue;
    }
}
