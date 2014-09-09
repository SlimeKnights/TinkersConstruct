package tconstruct.library.client;

public class StencilGuiElement extends GuiElement
{
    public final int stencilIndex;

    public StencilGuiElement(int buttonIconX, int buttonIconY, int stencilIndex, String domain, String texture)
    {
        super(buttonIconX, buttonIconY, domain, texture);

        this.stencilIndex = stencilIndex;
    }
}
