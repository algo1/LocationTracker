package com.lokeshponnada.locationtracker.background;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.lokeshponnada.locationtracker.database.LocationModel;
import com.lokeshponnada.locationtracker.database.TrackerDatabase;
import com.lokeshponnada.locationtracker.remoteconfig.AppConfig;
import com.lokeshponnada.locationtracker.repository.LocationRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lokesh on 15/02/18.
 */

public class LocationSyncJob extends Job {

    public static final String TAG = "location_job";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {

        // Iterate db
        LocationRepository repo = LocationRepository.getRepository(getContext());
        TrackerDatabase db = repo.getDb(getContext());

        List<LocationModel> locationList = db.locationDao().getNonPostedLocations();
        for(LocationModel model: locationList){
            LocationRepository.postLocation(model);
        }

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(LocationSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(AppConfig.SYNC_TIME), TimeUnit.MINUTES.toMillis(AppConfig.SYNC_GRACE_TIME))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
}
