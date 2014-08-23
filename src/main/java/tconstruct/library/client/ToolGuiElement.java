package tconstruct.library.client;

public class ToolGuiElement extends GuiElement
{
    public final int slotType;
    public final int[] iconsX;
    public final int[] iconsY;
    public final String title;
    public final String body;

    public ToolGuiElement(int st, int bx, int by, int[] xi, int[] yi, String t, String b, String d, String tex)
    {
        super(bx, by, d, tex);
        slotType = st;
        iconsX = xi;
        iconsY = yi;
        title = t;
        body = b;
    }
}
