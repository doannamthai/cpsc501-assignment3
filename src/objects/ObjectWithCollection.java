package objects;

import java.util.ArrayList;
import java.util.List;

public class ObjectWithCollection implements SupportObject{

    public List<SupportObject> list = new ArrayList();

    public List<SupportObject> getList() {
        return list;
    }

    public void setList(List<SupportObject> queue) {
        this.list = queue;
    }
}
