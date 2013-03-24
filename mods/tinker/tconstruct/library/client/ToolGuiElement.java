package mods.tinker.tconstruct.library.client;

public class ToolGuiElement
{
	public final int slotType;
	public final int buttonIconX;
	public final int buttonIconY;
	public final int[] iconsX;
	public final int[] iconsY;
	public final String title;
	public final String body;
	public final String texture;
	
	public ToolGuiElement(int st, int bx, int by, int[] xi, int[] yi, String t, String b)
	{
		slotType = st;
		buttonIconX = bx;
		buttonIconY = by;
		iconsX = xi;
		iconsY = yi;
		title = t;
		body = b;
		texture = "/mods/tinker/textures/gui/icons.png";
	}
	
	public ToolGuiElement(int st, int bx, int by, int[] xi, int[] yi, String t, String b, String tex)
	{
		slotType = st;
		buttonIconX = bx;
		buttonIconY = by;
		iconsX = xi;
		iconsY = yi;
		title = t;
		body = b;
		texture = tex;
	}
}
