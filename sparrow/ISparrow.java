package sparrow;

import net.minecraft.entity.Entity;

/** A comprehensive class for 
 * 
 * @author RaustBlackDragon, mDiyo
 */

public interface ISparrow
{
    /**Is this thing like a creeper, in that engaging it in combat has unexpected consequences? 
     * Would this entity and its allies be better off not fighting it at all? 
     * Set this to true if the attacker's combat abilities are basically a non-factor in what will happen to it if it fights this.
     */
    public boolean isStupidToAttack ();

    /**When a mod triggers an event that would set an entity to be dead with no reference to damage, should this entity be spared?*/
    public boolean doNotVaporize ();

    /**Does this entity attack non-player entities on sight?*/
    public boolean isPredator ();

    /**Does this entity attack the player on sight?*/
    public boolean isHostile ();

    /**Is this entity incapable of combat?*/
    public boolean isPeaceful ();

    /**Is this entity viable prey for a predator?*/
    public boolean isPrey ();

    /**Will this entity attack, but only when provoked?*/
    public boolean isNeutral ();

    /**Is this entity incapable of taking damage, and thus pointless to attack?*/
    public boolean isUnkillable ();

    /**Should this entity be considered a threat to par1entity?*/
    public boolean isThreatTo (Entity par1entity);

    /**Should this entity be considered a friend of par1entity?*/
    public boolean isFriendOf (Entity par1entity);

    /**Is this entity what people would generally consider to be an NPC?*/
    public boolean isNPC ();

    /**Is this a pet? 0 if not, 1 if it can be but isn't currently, 2 if it is.*/
    public int isPet ();

    /**Who is this pet's owner?*/
    public Entity getPetOwner ();

    /**What is the name of this individual entity?*/
    public String getName ();

    /**What is this entity currently targeting with intent to kill? Used to differentiate between the attack method monsters use and the attack method used for breeding and following*/
    public Entity getAttackingTarget ();

    /**What is the size of this entity? Multiply its two dimensions (X and Z are considered the same) in terms of blocks and put in the result ( a chicken would be .3 * .7, which is roughly .2)*/
    public float getSize ();

    /**What should this entity be referred to as? (Dog, Cat, Human, Enderman, etc.)*/
    public String getSpecies ();

    /**What is this entity's gender? 1 for male, 2 for female, 0 for neither*/
    public int getGender ();

    /**This is for mod-specific features. A mod can search for a response to a custom string, and you can add in whether or not they'll respond to it here, and what the response will be.*/
    public String customStringAndResponse (String s);

    /**Have this return the string you store your simplyID in. Use the function SimplyID.getNextSimplyID(this) to assign a simplyID to your entities that implement Sparrow.*/
    public String getSimplyID ();

}
