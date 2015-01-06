
package Zeno410Utils;

/**
 *
 * @author Zeno410
 */
public class Pair<TypeA,TypeB> {
    public TypeA a;
    public TypeB b;

    /** Creates a new instance of Pairing */
    public Pair(TypeA theA, TypeB theB) {a = theA; b= theB;}

    @Override
    public String toString() {return a.toString() + " " + b.toString();}

    public static <A,B> Pair<A,B> of(A a, B b) {
        return new Pair<A,B>(a,b);
    }

    @Override
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }

    @Override
    public boolean equals(Object compared) {
        if (compared instanceof Pair) {
            Pair<Object,Object> comparedPair = (Pair<Object,Object>)compared;
            if (a.equals(comparedPair.a)) {
                if (b.equals(comparedPair.b)) {
                    return true;
                }
            }
        }
        return false;
    }
}