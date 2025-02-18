package com.tacz.guns.client.animation;

public interface AnimationListener {
    /**
     * @param values When ChannelType is TRANSLATION, the length of values will be 3 and will store xyz offsets(NOT GLOBAL TRANSLATION!!! IT IS LOCAL OFFSET).
     *               When ChannelType is ROTATION, the length of values will be 4 and will store quaternions.(ALSO LOCAL ROTATION)
     *               When ChannelType is SCALE, the length of values will be 3 and will store xyz scale.(ALSO LOCAL SCALE)
     * @param blend  When blending, animation value should be accumulated, instead of being covered.
     */
    void update(float[] values, boolean blend);

    float[] recover();

    ObjectAnimationChannel.ChannelType getType();
}
