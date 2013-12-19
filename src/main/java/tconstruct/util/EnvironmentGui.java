package tconstruct.util;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnvironmentGui extends GuiScreen
{
    private GuiScreen parentGuiScreen;
    //private GuiTextField theGuiTextField;
    private final List<String> mods;
    StringBuilder builder = new StringBuilder();
    int count = 0;

    public EnvironmentGui(GuiScreen par1GuiScreen, List<String> modlist)
    {
        this.parentGuiScreen = par1GuiScreen;
        this.mods = modlist;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui ()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 175, this.height / 4 + 96 + 24, 350, 20, I18n.getString("I accept responsibility for all bugs and crashes in this modpack.")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 150, this.height / 4 + 120 + 24, 300, 20, I18n.getString("I will remove incompatible mods. (Exits Minecraft)")));
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed ()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed (GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                count++;
                if (count >= mods.size())
                    this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            if (button.id == 1)
            {
                System.exit(0);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen (int par1, int par2, float par3)
    {
        builder.setLength(0);
        this.drawDefaultBackground();
        String mod = mods.get(count);
        this.drawCenteredString(this.fontRenderer, I18n.getString("Tinkers' Construct and "+mod+" are not compatible"), this.width / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, I18n.getString("The following reasons are given:"), this.width / 2, 32, 0xFFFFFF);
        fontRenderer.drawSplitString(I18n.getString(EnvironmentChecks.modCompatDetails(mod, false)), 20, 52, 400, 0xffffff);

        this.drawCenteredString(this.fontRenderer, I18n.getString("Choose one."), this.width / 2, 165, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }
}
