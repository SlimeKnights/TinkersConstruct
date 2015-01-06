package Zeno410Utils;

/**
 *
 * @author Zeno410
 */
public interface Mutable<Type> extends Trackable<Type>{
    public void set(Type newValue);
    public Type value();
}
