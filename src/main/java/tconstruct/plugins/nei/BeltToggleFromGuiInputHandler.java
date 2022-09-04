package tconstruct.plugins.nei;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import tconstruct.client.ArmorControls;

public class BeltToggleFromGuiInputHandler implements IContainerInputHandler {

    static final String KEY_IDENTIFIER = "gui.tinkers_belt";
    public static void init() {
        API.addKeyBind(KEY_IDENTIFIER, 0);
        GuiContainerManager.addInputHandler(new BeltToggleFromGuiInputHandler());
    }

    @Override
    public boolean keyTyped(GuiContainer guiContainer, char c, int i) {
        if (i == 0) {
            return false;
        }
        final int keyBinding = NEIClientConfig.getKeyBinding(KEY_IDENTIFIER);
        if (keyBinding == 0 || i != keyBinding) {
            return false;
        }
        return ArmorControls.doBeltSwapIfPossible();
    }

    @Override
    public void onKeyTyped(GuiContainer guiContainer, char c, int i) {

    }

    @Override
    public boolean lastKeyTyped(GuiContainer guiContainer, char c, int i) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {
        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public void onMouseUp(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public boolean mouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public void onMouseDragged(GuiContainer guiContainer, int i, int i1, int i2, long l) {

    }
}
