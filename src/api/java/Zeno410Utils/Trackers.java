
package Zeno410Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.ref.WeakReference;

/**
 * @author Zeno410
 * This is a typesafe alternative to the Observer system where the observers will get garbage collected
 * without explicit work by the programmer. Trackers<Type> is used as an instance variable in a Trackable
 * object to provide the behavior of Observable. Any Acceptor<Type> replaces Observer.
 * You can also use Trackers with a different type to send limited, filtered, or transformed information out,
 * unlike the Observer implementation where it's the whole object or nothing at all.
 *
 * This stores weak references to object trackers so if they are no longer used elsewhere
 * they'll be dropped from here and go away as they should
 * IMPORTANT ** IMPORTANT ** IMPORTANT ** IMPORTANT ** IMPORTANT ** IMPORTANT *
 * For this reason there MUST be an external strong reference to the Acceptor or it goes away!
 * An object created in the informOnChange call will usually just get garbage collected and not work.
 *
 * E.g.
 *
 *     trackers.informOnChange(new InternalCallback());
 *
 * will generally fail as the newly created
 * InternalCallback will not be referred to by any other objects so it will go away and thus no longer
 * do the callback.
 *
 * A simple rule of thumb is to only pass an instance variable.
 *
 * And, yes, I learned this the hard way.
 */
public class Trackers<Type> {

    private HashMap<Key,WeakReference<Acceptor<Type>>> trackers =
            new HashMap<Key,WeakReference<Acceptor<Type>>>();
    private class Key {}

    public void informOnChange(Acceptor<Type> tracker) {
        for (Key key: trackers.keySet()) {
            Acceptor<Type> existing = trackers.get(key).get();
            if (tracker == existing ) {return;}
        }
        trackers.put(new Key(),new WeakReference<Acceptor<Type>>(tracker));
    }

    public int size() {return trackers.size();}

    public void update(Type toUpdate) {
        ArrayList<Key> toDelete = new ArrayList<Key>();
        for (Key key: trackers.keySet()) {
            Acceptor<Type> tracker = trackers.get(key).get();
            if (tracker == null ) {
                toDelete.add(key);
            } else {
                tracker.accept(toUpdate);
            }
        }
        for (Key key: toDelete) {
            trackers.remove(key);
        }
    }

    public void stopInforming(Acceptor<Type> tracker) {
        for (Key key: trackers.keySet()) {
            Acceptor<Type> existing = trackers.get(key).get();
            if (tracker == existing ) {
                trackers.put(key, new WeakReference<Acceptor<Type>>(null));
                return;
            }
        }
    }
}