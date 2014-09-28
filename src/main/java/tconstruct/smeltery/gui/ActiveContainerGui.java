package tconstruct.smeltery.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.smeltery.inventory.ActiveSlot;

public abstract class ActiveContainerGui extends GuiContainer
{

    public ActiveContainerGui(ActiveContainer p_i1072_1_)
    {
        super(p_i1072_1_);
    }

    @Override
    public void func_146977_a (Slot slot)
    {
        if (!(slot instanceof ActiveSlot) || ((ActiveSlot) slot).getActive())
        {
            super.func_146977_a(slot);
        }
    }

    @Override
    public boolean isMouseOverSlot (Slot slot, int mouseX, int mouseY)
    {
        if (!(slot instanceof ActiveSlot) || ((ActiveSlot) slot).getActive())
        {
            return super.isMouseOverSlot(slot, mouseX, mouseY);
        }
        return false;
    }

}
