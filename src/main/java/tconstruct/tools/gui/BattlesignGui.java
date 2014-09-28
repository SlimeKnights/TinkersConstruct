package tconstruct.tools.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.logic.BattlesignLogic;
import tconstruct.util.network.SignDataPacket;

public class BattlesignGui extends GuiScreen
{
    private BattlesignLogic battlesign;

    private float bgColR = 1F;
    private float bgColG = 1F;
    private float bgColB = 1F;
    private static ResourceLocation background = new ResourceLocation("tinker:textures/gui/battlesignText.png");
    private String[] text = { "", "", "", "", "" };
    int currentLine = 0;

    public BattlesignGui(BattlesignLogic logic)
    {
        this.battlesign = logic;

        ItemStack stack = logic.getEquipmentItem();
        if (stack != null)
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag("InfiTool");

            if (tag != null)
            {
                int head = tag.getInteger("Head");

                int bgCol = TConstructRegistry.getMaterial(head).primaryColor();

                bgColR = (float) (bgCol >> 16 & 255) / 255.0F;
                bgColG = (float) (bgCol >> 8 & 255) / 255.0F;
                bgColB = (float) (bgCol & 255) / 255.0F;
            }
        }
    }

    @Override
    public void initGui ()
    {
        buttonList.clear();

        super.initGui();

        Keyboard.enableRepeatEvents(true);

        int k = (this.width - 100) / 2;
        int l = (this.height - 110) / 2;

        buttonList.add(new GuiButton(0, k, l + 100, 100, 20, "Done"));
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float something)
    {
        Tessellator t = Tessellator.instance;

        GL11.glColor4f(bgColR, bgColG, bgColB, 1F);
        this.mc.getTextureManager().bindTexture(background);
        int k = (this.width - 100) / 2;
        int l = (this.height - 103) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, 100, 103);

        super.drawScreen(mouseX, mouseY, something);

        GL11.glPushMatrix();

        float lum = calcLuminance(bgColR, bgColG, bgColB);
        for (int i = 0; i < text.length; i++)
        {
            fontRendererObj.drawString((lum >= 35F ? EnumChatFormatting.BLACK : lum >= 31F ? EnumChatFormatting.GRAY : EnumChatFormatting.WHITE) + (i == currentLine ? "> " : "") + text[i] + (i == currentLine ? " \u00A7r" + (lum >= 35F ? EnumChatFormatting.BLACK : lum >= 31F ? EnumChatFormatting.GRAY : EnumChatFormatting.WHITE) + "<" : ""), k - fontRendererObj.getStringWidth((i == currentLine ? "> " : "") + text[i] + (i == currentLine ? " <" : "")) / 2 + 51, l + 4 + 10 * i, 0);
        }

        GL11.glPopMatrix();
    }

    @Override
    protected void keyTyped (char c, int i)
    {
        super.keyTyped(c, i);

        if (fontRendererObj.getStringWidth(text[currentLine]) < 90 && ChatAllowedCharacters.isAllowedCharacter(c))
        {
            if (Keyboard.isKeyDown(56) && c == 'f' && (text[currentLine].length() == 0 || text[currentLine].charAt(text[currentLine].length() - 1) != '\u00A7'))
            {
                text[currentLine] += "\u00A7";
            }
            else
            {
                text[currentLine] += c;
            }
        }
        else
        {
            switch (i)
            {
            case 14:
                if (text[currentLine].length() > 0)
                {
                    text[currentLine] = text[currentLine].substring(0, text[currentLine].length() - 1);
                }

                break;
            case 28:
                moveLine(1);
                break;
            case 200:
                moveLine(-1);
                break;
            case 208:
                moveLine(1);
                break;
            }
        }
    }

    private void moveLine (int i)
    {
        if (i < 0)
        {
            if (currentLine <= 0)
            {
                currentLine = text.length - 1;
            }
            else
            {
                currentLine--;
            }
        }
        else
        {
            if (currentLine >= text.length - 1)
            {
                currentLine = 0;
            }
            else
            {
                currentLine++;
            }
        }
    }

    @Override
    public void onGuiClosed ()
    {
        super.onGuiClosed();

        Keyboard.enableRepeatEvents(false);

        TConstruct.packetPipeline.sendToServer(new SignDataPacket(battlesign.getWorldObj().provider.dimensionId, battlesign.xCoord, battlesign.yCoord, battlesign.zCoord, text));
    }

    private float calcLuminance (float r, float g, float b)
    {
        return (r * 255 * 0.299f + g * 255 * 0.587f + b * 255 * 0.114f) / 3;
    }

    @Override
    protected void actionPerformed (GuiButton button)
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
        }
    }
}
