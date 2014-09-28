package tconstruct.library.client;

public class GuiElement
{
    public final int buttonIconX;
    public final int buttonIconY;
    public final String domain;
    public final String texture;

    public GuiElement(int buttonIconX, int buttonIconY, String domain, String texture)
    {
        this.buttonIconX = buttonIconX;
        this.buttonIconY = buttonIconY;
        this.domain = domain;
        this.texture = texture;
    }
}
