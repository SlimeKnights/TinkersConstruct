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
        this.field_146292_n.clear();
        this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 175, this.field_146295_m / 4 + 96 + 24, 350, 20, I18n.getStringParams("I accept responsibility for all cross-mod bugs in this modpack.")));
        this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 175, this.field_146295_m / 4 + 120 + 24, 350, 20, I18n.getStringParams("I do not want to run these mods together. (Exits Minecraft)")));
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
        if (button.field_146124_l)
        {
            if (button.field_146127_k == 0)
            {
                count++;
                if (count >= mods.size())
                    this.field_146297_k.func_147108_a(this.parentGuiScreen);
            }
            if (button.field_146127_k == 1)
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
        this.func_146276_q_();
        String mod = mods.get(count);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("Tinkers' Construct is not compatible with "+mod), this.field_146294_l / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("The following reasons are given:"), this.field_146294_l / 2, 32, 0xFFFFFF);
        field_146289_q.drawSplitString(I18n.getStringParams(EnvironmentChecks.modCompatDetails(mod, false)), 20, 52, 400, 0xffffff);

        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("The Tinkers' Construct team will not accept bug reports with this mod installed."), this.field_146294_l / 2, 153, 0xFFFFFF);
        this.drawCenteredString(this.field_146289_q, I18n.getStringParams("We apologize for any inconvenience this may cause you."), this.field_146294_l / 2, 165, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }
}
