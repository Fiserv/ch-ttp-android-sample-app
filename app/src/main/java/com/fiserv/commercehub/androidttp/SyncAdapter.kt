package com.fiserv.commercehub.androidttp

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import androidx.work.WorkerParameters
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.security.ProviderInstaller


/**
 * Sample activity using {@link ProviderInstaller}.
 */
class SyncAdapter(appContext: Context, workerParams: WorkerParameters):
    AbstractThreadedSyncAdapter(appContext, true) {

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult
    ) {
        try {
            ProviderInstaller.installIfNeeded(getContext())
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play services is out of date, disabled, etc.

// Prompt the user to install/update/enable Google Play services.

            GooglePlayServicesUtil.showErrorNotification(e.connectionStatusCode, getContext())

            // Notify the SyncManager that a soft error occurred.
            syncResult.stats.numIoExceptions++
            return
        } catch (e: GooglePlayServicesNotAvailableException) {
// Indicates a non-recoverable error; the ProviderInstaller is not able
// to install an up-to-date Provider.

// Notify the SyncManager that a hard error occurred.

            syncResult.stats.numAuthExceptions++
            return
        }

// If this is reached, you know that the provider was already up-to-date,
// or was successfully updated.
    }
}