package org.nexo.motor.core.session

fun referenceImage(
    startingPosition: Int,
    isFirstEventOfSession: Boolean,
    previousFrameImage: String?,
    zeroMarkImage: String,
    lastFilledImageOfPreviousEvent: String?,
): String = when {
    startingPosition > 1 -> previousFrameImage ?: zeroMarkImage
    isFirstEventOfSession -> zeroMarkImage
    else -> lastFilledImageOfPreviousEvent ?: zeroMarkImage
}
