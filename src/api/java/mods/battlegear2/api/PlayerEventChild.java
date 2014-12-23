package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public abstract class PlayerEventChild extends PlayerEvent{

    /**
     * The event that this event is a child of
     */
	public final PlayerEvent parent;

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

    public EntityPlayer getPlayer(){
        return parent.entityPlayer;
    }
    
	/**
	 * Event fired when a shield successfully blocks an attack (in {@link LivingHurtEvent})
	 */
	 public static class ShieldBlockEvent extends PlayerEventChild {
	 	public final ItemStack shield;
	 	public final DamageSource source;
	 	public final float ammount; // use same name as other Forge events
	 	public float ammountRemaining = 0.0F; // damage remaining after shield block event, if any
        /**
         * If the {@link IShield#blockAnimation(EntityPlayer, float)} should be called
         */
        public boolean performAnimation = true;
        /**
         * If the shield should be damaged based on the ammount and the result of {@link IShield#getDamageReduction(ItemStack, DamageSource)}
         */
        public boolean damageShield = true;
	 	public ShieldBlockEvent(PlayerEvent parent, ItemStack shield, DamageSource source, float ammount) {
	 		super(parent);
	 		this.shield = shield;
	 		this.source = source;
	 		this.ammount = ammount;
	 	}
	 }

    /**
     * Called when a player right clicks in battlemode
     * The parent event can be either {@link PlayerInteractEvent} or {@link EntityInteractEvent} if the OffhandAttackEvent allowed swinging
     * Both {@link ItemStack} can be null
     * If cancelled, no offhand swinging will be performed
     */
    @Cancelable
    public static class OffhandSwingEvent extends PlayerEventChild {
        public final ItemStack mainHand;
        public final ItemStack offHand;
        public OffhandSwingEvent(PlayerEvent parent, ItemStack mainHand, ItemStack offHand){
            super(parent);
            this.mainHand = mainHand;
            this.offHand = offHand;
        }
    }

    /**
     * Called when a player right clicks an entity in battlemode
     * Both {@link ItemStack} can be null
     * Cancelling will prevent any further processing and prevails over the boolean fields
     */
    @Cancelable
    public static class OffhandAttackEvent extends PlayerEventChild {

        /**
         * If we should call the OffhandSwingEvent and perform swinging
         */
        public boolean swingOffhand = true;
        /**
         * If we should perform an attack on the entity with the offhand item
         * Note: Will post {@link AttackEntityEvent} and {@link Item#onLeftClickEntity(ItemStack, EntityPlayer, Entity)}
         * with {@link InventoryPlayer#currentItem} offset to the offhand
         */
        public boolean shouldAttack = true;
        /**
         * If we should cancel the base entity interaction event
         */
        public boolean cancelParent = true;
        /**
         * The base entity interaction event
         */
        public final EntityInteractEvent event;
        /**
         * Content of the main hand slot
         */
        public final ItemStack mainHand;
        /**
         * Content of the off hand slot
         */
        public final ItemStack offHand;

        public OffhandAttackEvent(EntityInteractEvent parent, ItemStack mainHand, ItemStack offHand) {
            super(parent);
            this.event = parent;
            this.mainHand = mainHand;
            this.offHand = offHand;
        }

        public Entity getTarget() {
            return ((EntityInteractEvent)parent).target;
        }
    }

    /**
     * This event replicates the event usage of {@link PlayerInteractEvent} for the item in left hand on right click,
     * allowing support for other mods that use such event to customize item usage
     * {@link Item#onItemUseFirst}, {@link Item#onItemRightClick} and {@link Item#onItemUse} will then get called the same way as with the item in the player right hand for PlayerInteractEvent
     */
    @Cancelable
    public static class UseOffhandItemEvent extends PlayerEventChild{
        /**
         * The {@link ItemStack} held in left hand
         */
        public final ItemStack offhand;
        /**
         * The equivalent {@link PlayerInteractEvent} that would have been triggered if the offhand item was held in right hand and right click was pressed
         */
        public final PlayerInteractEvent event;
        public UseOffhandItemEvent(PlayerInteractEvent event, ItemStack offhand){
            super(event);
            this.event = event;
            this.offhand = offhand;
        }
    }

    public static class QuiverArrowEvent extends PlayerEventChild{
        /**
         * The event from which this occurred
         */
        protected final ArrowLooseEvent event;
        public QuiverArrowEvent(ArrowLooseEvent event){
            super(event);
            this.event = event;
        }

        /**
         * @return the player using the bow
         */
        public EntityPlayer getArcher(){
            return getPlayer();
        }

        /**
         * @return the bow trying to fire
         */
        public ItemStack getBow()
        {
            return event.bow;
        }

        /**
         * @return the amount of charge in the bow
         */
        public float getCharge()
        {
            return event.charge;
        }

        /**
         * Event fired after an arrow has been selected and taken from a {@link IArrowContainer2}, before it is actually spawned
         */
        @Cancelable
        public static class Firing extends QuiverArrowEvent {
            /**
             * Damage done to the bow after arrow is fired
             */
            public int bowDamage = 1;
            /**
             * The volume of the sound emitted from the bow after arrow is fired
             */
            public float bowSoundVolume = 1.0F;
            /**
             * Decides if standard enchantments can be added to the arrow
             */
            public boolean addEnchantments = true;
            /**
             * Decides if critical state should be forced into the arrow
             */
            public boolean isCritical = false;
            /**
             * The quiver from which the arrow was pulled from
             */
            public final ItemStack quiver;
            /**
             * The arrow to be fired, can't be null
             */
            public final EntityArrow arrow;

            public Firing(ArrowLooseEvent parent, ItemStack quiver, EntityArrow arrow) {
                super(parent);
                this.quiver = quiver;
                this.arrow = arrow;
            }

        }

        /**
         * The DEFAULT result for this event is the vanilla charge calculated value
         * Use setNewCharge to override the value with the one provided
         * Change the event result to DENY to prevent further processing
         */
        @HasResult
        public static class ChargeCalculations extends QuiverArrowEvent {
            /**
             * Returned value if the result is set to allow
             */
            protected float charge;
            public ChargeCalculations(ArrowLooseEvent event){
                super(event);
            }

            @Override
            public float getCharge(){
                MinecraftForge.EVENT_BUS.post(this);
                switch (this.getResult()){
                    case ALLOW:
                        return charge;
                    case DENY:
                        return 0;
                }
                float f = super.getCharge()/20.0F;
                f = (f * f + f * 2.0F) / 3.0F;
                if ((double)f < 0.1D)
                {
                    return 0;
                }
                if (f > 1.0F)
                {
                    f = 1.0F;
                }
                return f;
            }

            public void setNewCharge(float charge){
                this.setResult(Result.ALLOW);
                this.charge = charge;
            }
        }
    }
}
