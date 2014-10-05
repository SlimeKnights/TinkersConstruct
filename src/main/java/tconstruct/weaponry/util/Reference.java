package boni.tinkersweaponry.util;

public final class Reference {
    private Reference() {}

    public static final String MOD_ID = "tinkersweaponry";
    public static final String MOD_NAME = "Tinker's Weaponry";
    public static final String TCON_MOD_ID = "TConstruct";

    public static final String RESOURCE = MOD_ID.toLowerCase();

    public static String resource(String res)
    {
        return String.format("%s:%s", RESOURCE, res);
    }
    public static String prefix(String name) { return String.format("tcon.weaponry.%s", name);}
}
