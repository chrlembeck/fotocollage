package org.lembeck.photocollage.event;

public sealed interface ImageComposeProgressEvent permits ComposeFinishedEvent, ComposeStartedEvent, ImagePaintedEvent, NewRatioFoundEvent, StartAttemptEvent {

}