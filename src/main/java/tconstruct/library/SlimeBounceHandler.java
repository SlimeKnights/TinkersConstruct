package tconstruct.library;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.IdentityHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import tconstruct.gadgets.TinkerGadgets;
import tconstruct.gadgets.item.ItemSlimeBoots;

/** Logic for entities bouncing */
public class SlimeBounceHandler {

    public static final float SQRT_2 = sqrt(2.0F);
    private static final float[] SIN_TABLE = new float[65536];
    private static final double FRAC_BIAS;
    private static final double[] ASINE_TAB;
    private static final double[] COS_TAB;
    public static final IdentityHashMap<EntityLivingBase, BounceInfo> BOUNCING_ENTITIES =
            new IdentityHashMap<EntityLivingBase, BounceInfo>();

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin(i * Math.PI * 2.0D / 65536.0D);
        }
        FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
        ASINE_TAB = new double[257];
        COS_TAB = new double[257];

        for (int j = 0; j < 257; ++j) {
            double d0 = j / 256.0D;
            double d1 = Math.asin(d0);
            COS_TAB[j] = Math.cos(d1);
            ASINE_TAB[j] = d1;
        }
    }

    private SlimeBounceHandler() {
        TinkerGadgets.log.info("Created instance of Bounce Handler.");
    }

    /** Registers event handlers */
    public static void init() {
        TinkerGadgets.log.info("Registering Bounce Handler.");
        registerEvent(new SlimeBounceHandler());
    }

    /**
     * Preserves entity air momentum
     *
     * @param entity Entity to bounce
     */
    public static void addBounceHandler(EntityLivingBase entity) {
        addBounceHandler(entity, 0d);
    }

    /**
     * Causes the entity to bounce, needed because the fall event will reset motion
     * afterwards
     *
     * @param entity Entity to bounce
     * @param bounce Bounce amount
     */
    public static void addBounceHandler(EntityLivingBase entity, double bounce) {
        // no fake players PlayerTick event
        if (entity instanceof FakePlayer) {
            return;
        }
        // update bounce info
        BounceInfo info = BOUNCING_ENTITIES.get(entity);
        if (hasSlimeBoots(entity)) {
            if (info == null) {
                BOUNCING_ENTITIES.put(entity, new BounceInfo(entity, bounce));
                TinkerGadgets.log.info("Created new BounceInfo for " + entity.getCommandSenderName());
            } else if (bounce != 0) {
                TinkerGadgets.log.info("Updating Bounce data?");
                // updated bounce if needed
                info.bounce = bounce;
                // add one to the tick as there is a 1 tick delay between falling and ticking
                // for many entities
                info.bounceTick = entity.ticksExisted + 1;

                info.lastMagSq = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
                info.lastAngle = atan2(entity.motionZ, entity.motionX);
            }
        }
    }

    public static void registerEvent(Object o) {
        MinecraftForge.EVENT_BUS.register(o);
        FMLCommonHandler.instance().bus().register(o);
    }

    /** Called on living tick to preserve momentum and bounce */
    @SubscribeEvent
    public void onLivingTick(LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity != null) {
            BounceInfo info = BOUNCING_ENTITIES.get(entity);

            // TinkerGadgets.log.info("Bounce Data valid? "+(info != null));
            // if we have info for this entity, time to work
            if (info != null) {
                // TinkerGadgets.log.info("Found Bounce Data in tick handler.");
                // if flying, nothing to do
                if (entity.isDead || entity.noClip) {
                    // TinkerGadgets.log.info("Removing Bounce Data.");
                    BOUNCING_ENTITIES.remove(entity);
                    return;
                }
                if (!hasSlimeBoots(entity)) {
                    // TinkerGadgets.log.info("Removing Bounce Data. No Boots.");
                    BOUNCING_ENTITIES.remove(entity);
                    return;
                }

                // if its the bounce tick, time to bounce. This is to circumvent the logic that
                // resets y motion after landing
                if (entity.ticksExisted == info.bounceTick) {
                    TinkerGadgets.log.info("Bounce Tick?");
                    entity.motionY = info.bounce;
                    info.bounceTick = 0;
                }

                boolean isInAir = !entity.onGround && !entity.isInWater() && !entity.isOnLadder();

                // TinkerGadgets.log.info("In Air? "+isInAir);
                // preserve motion
                if (isInAir && info.lastMagSq > 0) {
                    // figure out how much motion has reduced
                    double motionSq = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
                    // if not moving, cancel velocity preserving in 5 ticks
                    if (motionSq == 0) {
                        if (info.stopMagTick == 0) {
                            info.stopMagTick = entity.ticksExisted + 5;
                        } else if (entity.ticksExisted > info.stopMagTick) {
                            info.lastMagSq = 0;
                        }
                    } else if (motionSq < info.lastMagSq) {
                        info.stopMagTick = 0;
                        // preserve 95% of former speed
                        double boost = Math.sqrt(info.lastMagSq / motionSq) * 0.95f;
                        if (boost > 1) {
                            entity.motionX *= boost;
                            entity.motionZ *= boost;
                            entity.isAirBorne = true;
                            info.lastMagSq = info.lastMagSq * 0.95f * 0.95f;
                            // play sound if we had a big angle change
                            double newAngle = atan2(entity.motionZ, entity.motionX);
                            if (Math.abs(newAngle - info.lastAngle) > 1) {
                                entity.playSound("mob.slime.small", 1.0f, 1.0f);
                            }
                            info.lastAngle = newAngle;
                        } else {
                            info.lastMagSq = motionSq;
                            info.lastAngle = atan2(entity.motionZ, entity.motionX);
                        }
                    }
                }
                // TinkerGadgets.log.info("Did things? 1");

                // timing the effect out
                if (info.wasInAir && !isInAir) {
                    if (info.endHandler == 0) {
                        info.endHandler = entity.ticksExisted + 5;
                    } else if (entity.ticksExisted > info.endHandler) {
                        // TinkerGadgets.log.info("Removing Bounce Data for "+entity.getCommandSenderName());
                        BOUNCING_ENTITIES.remove(entity);
                    }
                } else {
                    info.endHandler = 0;
                    info.wasInAir = true;
                }
                // TinkerGadgets.log.info("Did things? 2");
            } else {
                // TinkerGadgets.log.info("Bad Bounce Data.");
            }
        }
    }

    public static Vec3 getMotion(EntityLivingBase aEntity) {
        return Vec3.createVectorHelper(aEntity.motionX, aEntity.motionY, aEntity.motionZ);
    }

    public static boolean hasSlimeBoots(EntityLivingBase entity) {
        if (entity != null) {
            for (int i = 1; i < 5; i++) {
                ItemStack aBoots = entity.getEquipmentInSlot(i);
                if (aBoots != null && aBoots.getItem() instanceof ItemSlimeBoots) {
                    // TinkerGadgets.log.info("Found boots in slot "+i);
                    return true;
                }
            }
        }
        return false;
    }

    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }

    public static double atan2(double aArg1, double aArg2) {
        double d0 = aArg2 * aArg2 + aArg1 * aArg1;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        } else {
            boolean flag = aArg1 < 0.0D;
            if (flag) {
                aArg1 = -aArg1;
            }
            boolean flag1 = aArg2 < 0.0D;
            if (flag1) {
                aArg2 = -aArg2;
            }
            boolean flag2 = aArg1 > aArg2;
            if (flag2) {
                double d1 = aArg2;
                aArg2 = aArg1;
                aArg1 = d1;
            }
            double d9 = fastInvSqrt(d0);
            aArg2 = aArg2 * d9;
            aArg1 = aArg1 * d9;
            double d2 = FRAC_BIAS + aArg1;
            int i = (int) Double.doubleToRawLongBits(d2);
            double d3 = ASINE_TAB[i];
            double d4 = COS_TAB[i];
            double d5 = d2 - FRAC_BIAS;
            double d6 = aArg1 * d4 - aArg2 * d5;
            double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
            double d8 = d3 + d7;
            if (flag2) {
                d8 = (Math.PI / 2D) - d8;
            }
            if (flag1) {
                d8 = Math.PI - d8;
            }
            if (flag) {
                d8 = -d8;
            }
            return d8;
        }
    }

    public static double fastInvSqrt(double aArg) {
        double d0 = 0.5D * aArg;
        long i = Double.doubleToRawLongBits(aArg);
        i = 6910469410427058090L - (i >> 1);
        aArg = Double.longBitsToDouble(i);
        aArg = aArg * (1.5D - d0 * aArg * aArg);
        return aArg;
    }

    /** Data class to keep track of bouncing info for an entity */
    private static class BounceInfo {
        /** Velocity the entity should have, unused if 0 */
        private double bounce;
        /** Time to update the entities velocity */
        private int bounceTick;
        /** Tick to stop entity magnitude changes */
        private int stopMagTick;
        /** Magnitude of the X/Z motion last tick */
        private double lastMagSq;
        /** If true, the entity was in air last tick */
        private boolean wasInAir = false;
        /** Time when motion should stop */
        private int endHandler = 0;
        /** Last angle of motion, used for sound effects */
        private double lastAngle;

        public BounceInfo(EntityLivingBase entity, double bounce) {
            this.bounce = bounce;
            if (bounce != 0) {
                // add one to the tick as there is a 1 tick delay between falling and ticking
                // for many entities
                this.bounceTick = entity.ticksExisted + 1;
            } else {
                this.bounceTick = 0;
            }
            this.lastMagSq = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
        }
    }
}
