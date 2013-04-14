package mods.tinker.tconstruct.client.gui;

import org.w3c.dom.*;

public abstract class BookPage
{
    public abstract void readPageFromXML(Element element);    
    public void renderBackgroundLayer(int localwidth, int localheight) {}
    public abstract void renderContentLayer(int localwidth, int localheight);
}
