
package Zeno410Utils;

/**
 *
 * @author Zeno410
 */
public abstract class Acceptor<Type> {
    public abstract void accept(Type accepted);

    public static class Ignorer<IgnoredType> extends Acceptor<IgnoredType> {
        public void accept(IgnoredType ignored) {}
    }

    public static class OneShotRedirector<RedirectedType> extends Acceptor<RedirectedType> {
        private Acceptor<RedirectedType> target;
        public void accept(RedirectedType redirected) {
            target.accept(redirected);
            target = null;
        }
        public void redirectTo(Acceptor<RedirectedType> newTarget) {
            target = newTarget;
        }
    }
}
