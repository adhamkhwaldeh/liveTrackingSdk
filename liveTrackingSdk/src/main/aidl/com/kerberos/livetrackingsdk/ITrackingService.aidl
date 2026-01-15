package com.kerberos.livetrackingsdk;

interface ITrackingService {

    boolean startTracking();

    boolean pauseTracking();

    boolean resumeTracking();

    boolean stopTracking();

}