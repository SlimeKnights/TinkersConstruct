package mods.battlegear2.api;

import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerEventChild extends PlayerEvent{

	public PlayerEvent parent;

	public PlayerEventChild(PlayerEvent parent) {
		super(parent.entityPlayer);
		this.parent = parent;
	}

    public void setCancelParentEvent(boolean cancel) {
        parent.setCanceled(cancel);
    }

    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
        parent.setCanceled(cancel);
    }

    @Override
    public void setResult(Result value) {
        super.setResult(value);
        parent.setResult(value);
    }
}
