package tconstruct.library.client;

public class PatternGuiElement
{
    //Position
    public final int buttonIconX;
    public final int buttonIconY;
    //Name
    public final String title;
    //Tooltip
    public final String body;
    //Mod directory for the texture
    public final String domain;
    //Texture path, from assets/<domain>/
    public final String texture;

    public PatternGuiElement (int bx, int by, String t, String b, String d, String tex)
    {
        buttonIconX = bx;
        buttonIconY = by;
        title = t;
        body = b;
        domain = d;
        texture = tex;
    }
}
