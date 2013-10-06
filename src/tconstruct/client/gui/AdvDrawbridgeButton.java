package tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;

public class AdvDrawbridgeButton extends GuiButton {
	protected static final ResourceLocation buttonTextures = new ResourceLocation("tinker:textures/gui/slotButton.png");

	/** Button width in pixels */
	protected int width;

	/** Button height in pixels */
	protected int height;

	/** The x position of this control. */
	public int xPosition;

	/** The y position of this control. */
	public int yPosition;
	
	/** The x position of this control when the GUI is expanded. */
	public int xPositionExp;
	
	/** The x position of this control when the GUI is expanded. */
	public int yPositionExp;
	
	/** Is the GUI expanded. */
	public boolean isGuiExpanded = false;
	
	/** The string displayed on this control. */
	public String displayString;

	/** ID for this control. */
	public int id;

	/** True if this control is enabled, false to disable. */
	public boolean enabled;

	/** Hides the button completely if false. */
	public boolean drawButton;
	protected boolean field_82253_i;

	public AdvDrawbridgeButton(int par1, int par2, int par3, int par4, int par5, String par6Str) {
		this(par1, par2, par3, par4, par5, 200, 20, par6Str);
	}

	public AdvDrawbridgeButton(int par1, int par2, int par3, int par4, int par5, int par6, int par7, String par8Str) {
		super(par1, par2, par3, par6, par7, par8Str);
		this.width = 200;
		this.height = 20;
		this.enabled = true;
		this.drawButton = true;
		this.id = par1;
		this.xPosition = par2;
		this.yPosition = par3;
		this.xPositionExp = par4;
		this.yPositionExp = par5;
		this.width = par6;
		this.height = par7;
		this.displayString = par8Str;
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over
	 * this button and 2 if it IS hovering over this button.
	 */
	protected int getHoverState(boolean par1) {
		byte b0 = 1;

		if (!this.enabled) {
			b0 = 0;
		} else if (par1) {
			b0 = 2;
		}

		return b0;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		if (this.drawButton) {
			FontRenderer fontrenderer = par1Minecraft.fontRenderer;
			par1Minecraft.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			int xPosition = isGuiExpanded ? this.xPositionExp : this.xPosition;
			int yPosition = isGuiExpanded ? this.yPositionExp : this.yPosition;
			
			this.field_82253_i = par2 >= xPosition && par3 >= yPosition && par2 < xPosition + this.width && par3 < yPosition + this.height;
			int k = this.getHoverState(this.field_82253_i);
			this.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 26, this.width / 2, this.height);
			this.drawTexturedModalRect(xPosition + this.width / 2, yPosition, 200 - this.width / 2, 46 + k * 26, this.width / 2, this.height);
			this.mouseDragged(par1Minecraft, par2, par3);
			int l = 14737632;

			if (!this.enabled) {
				l = -6250336;
			} else if (this.field_82253_i) {
				l = 16777120;
			}

			this.drawCenteredString(fontrenderer, this.displayString, xPosition + this.width / 2, yPosition + (this.height - 8) / 2, l);
		}
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int par1, int par2) {
	}

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of
	 * MouseListener.mousePressed(MouseEvent e).
	 */
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
		int xPosition = isGuiExpanded ? this.xPositionExp : this.xPosition;
		int yPosition = isGuiExpanded ? this.yPositionExp : this.yPosition;
		
		return this.enabled && this.drawButton && par2 >= xPosition && par3 >= yPosition && par2 < xPosition + this.width && par3 < yPosition + this.height;
	}

	public boolean func_82252_a() {
		return this.field_82253_i;
	}

	public void func_82251_b(int par1, int par2) {
	}
}