
package Zeno410Utils;

/**
 *
 * @author Zeno410
 */
public class ConcreteMutable<Type> implements Mutable<Type> {
    private Type data;
    private final Trackers<Type> trackers = new Trackers<Type>();

    public ConcreteMutable(Type initialValue) {
        set(initialValue);
    }

    public ConcreteMutable() {}
    
    public void set(Type newValue) {
        if ((data==null&&newValue != null)||(data.equals(value()))){
            data = newValue;
            trackers.update(data);
        }
    }

    public Type value() {
        return data;
    }

    public void informOnChange(Acceptor<Type> target) {
        trackers.informOnChange(target);
    }

    public void stopInforming(Acceptor<Type> target) {
        trackers.stopInforming(target);
    }


}
