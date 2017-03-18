package org.thoughtcrime.securesms.dependencies;

import org.thoughtcrime.securesms.jobs.GcmRefreshJob;
import org.thoughtcrime.securesms.jobs.RefreshAttributesJob;

import dagger.Module;

@Module(complete = false, injects = {GcmRefreshJob.class,
                                     RefreshAttributesJob.class})
public class RedPhoneCommunicationModule {


}
