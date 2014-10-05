package tconstruct.util;

import tconstruct.TConstruct;

public final class Reference {
    private Reference() {}

    public static final String MOD_ID = TConstruct.modID;
    public static final String MOD_NAME = "Tinkers' Construct";

    public static final String RESOURCE = MOD_ID.toLowerCase();

    public static String resource(String res)
    {
        return String.format("%s:%s", RESOURCE, res);
    }
    public static String prefix(String name) { return String.format("tconstruct.%s", name);}
}
