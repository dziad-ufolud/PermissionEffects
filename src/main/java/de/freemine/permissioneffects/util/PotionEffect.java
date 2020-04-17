package de.freemine.permissioneffects.util;

public enum PotionEffect {


    SPEED(1),

    /**
     * Decreases movement speed.
     */
    SLOW(2),

    /**
     * Increases dig speed.
     */
    FAST_DIGGING(3),

    /**
     * Decreases dig speed.
     */
    SLOW_DIGGING(4),

    /**
     * Increases damage dealt.
     */
    INCREASE_DAMAGE(5),

    /**
     * Heals an entity.
     */
    HEAL(6),

    /**
     * Hurts an entity.
     */
    HARM(7),

    /**
     * Increases jump height.
     */
    JUMP(8),

    /**
     * Warps vision on the client.
     */
    CONFUSION(9),

    /**
     * Regenerates health.
     */
    REGENERATION(10),

    /**
     * Decreases damage dealt to an entity.
     */
    DAMAGE_RESISTANCE(11),

    /**
     * Stops fire damage.
     */
    FIRE_RESISTANCE(12),

    /**
     * Allows breathing underwater.
     */
    WATER_BREATHING(13),

    /**
     * Grants invisibility.
     */
    INVISIBILITY(14),

    /**
     * Blinds an entity.
     */
    BLINDNESS(15),

    /**
     * Allows an entity to see in the dark.
     */
    NIGHT_VISION(16),

    /**
     * Increases hunger.
     */
    HUNGER(17),

    /**
     * Decreases damage dealt by an entity.
     */
    WEAKNESS(18),

    /**
     * Deals damage to an entity over time.
     */
    POISON(19),

    /**
     * Deals damage to an entity over time and gives the health to the
     * shooter.
     */
    WITHER(20),

    /**
     * Increases the maximum health of an entity.
     */
    HEALTH_BOOST(21),

    /**
     * Increases the maximum health of an entity with health that cannot be
     * regenerated, but is refilled every 30 seconds.
     */
    ABSORPTION(22),

    /**
     * Increases the food level of an entity each tick.
     */
    SATURATION(23),

    /**
     * Outlines the entity so that it can be seen from afar.
     */
    GLOWING(24),

    /**
     * Causes the entity to float into the air.
     */
    LEVITATION(25),

    /**
     * Loot table luck.
     */
    LUCK(26),

    /**
     * Loot table unluck.
     */
    UNLUCK(27),

    //1.13
    /**
     * Decreases falling speed and negates fall damage.
     */
    SLOW_FALLING(28),

    /**
     * Increases underwater visibility and mining speed, prevents drowning.
     */
    CONDUIT_POWER(29),
    
    DOLPHINS_GRACE(30);


    private final int id;

    PotionEffect(int i) {
        this.id = i;
    }


    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
