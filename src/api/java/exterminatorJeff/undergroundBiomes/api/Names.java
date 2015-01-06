package exterminatorJeff.undergroundBiomes.api;


public class Names {

    private final String internal;

    public Names(String internalName) {
        super();
        internal = internalName;
    }

    public final String internal() {
        return internal;
    }

    public final String external() {
        return UBIDs.publicName(internal);
    }

    public final String iconName() {
        return UBIDs.iconName(internal);
    }

    public final RuntimeException duplicateRegistry() {
        return new RuntimeException("duplication registry for Block " + external());
    }

    @Override
    public String toString() {return external();}
}
