package mods.battlegear2.api.weapons;

import mods.battlegear2.api.PlayerEventChild;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class OffhandAttackEvent extends PlayerEventChild {

    public boolean swingOffhand = true;
    public boolean shouldAttack = true;
    public EntityInteractEvent event;

    public OffhandAttackEvent(EntityInteractEvent parent) {
        super(parent);
        this.event = parent;
    }

	public Entity getTarget() {
        return event.target;
    }
}