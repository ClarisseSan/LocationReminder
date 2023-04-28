package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.BuildConfig
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private val TAG = SaveReminderFragment::class.java.simpleName

    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        checkPermissionsAndStartGeofencing()
    }

    /**
     * Starts the permission check and Geofence process only if the Geofence associated with the
     * current hint isn't yet active.
     */
    private fun checkPermissionsAndStartGeofencing() {
        //TODO uncomment
        // if (_viewModel.geofenceIsActive()) return

        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

//            TODO: use the user entered reminder details to:
//             2) save the reminder to the local db
            //ReminderDataItem(title, description, location, latitude, longitude)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        //First, check if the ACCESS_FINE_LOCATION permission is granted.
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))


        //If the device is running Q or higher, check that the ACCESS_BACKGROUND_LOCATION permission is granted.
        // Return true if the device is running lower than Q where you don't need a permission to access location in the background.
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            context!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }

        //Return true if the permissions are granted and false if not
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        //If the permissions have already been approved, you don’t need to ask again. Return out of the method.
        if (foregroundAndBackgroundLocationPermissionApproved())
            return

        //The permissionsArray contains the permissions that are going to be requested.
        // Initially, add ACCESS_FINE_LOCATION since that will be needed on all API levels.
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        //Below it, you will need a resultCode. The code will be different depending on if the device is running Q
        // or later and will inform us if you need to check for one permission (fine location)
        // or multiple permissions (fine and background location) when the user returns from the permission request screen.
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }

            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Log.d(TAG, "Request foreground only location permission ${resultCode.toString()}")

        //Request permissions passing in the permissions array and the result code.
        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    //Once the user responds to the permissions, you will need to handle their response using the onRequestPermissionsResult().
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")
        //Permissions can be denied in a few ways:
        //If the grantResults array is empty, then the interaction was interrupted and the permission request was cancelled.
        //If the grantResults array’s value at the LOCATION_PERMISSION_INDEX has a PERMISSION_DENIED it means that the user denied foreground permissions.
        //If the request code equals REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE and the BACKGROUND_LOCATION_PERMISSION_INDEX is denied it means that the device is running API 29 or above and that background permissions were denied.

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            //This app has very little use when permissions are not granted so present a snackbar
            // explaining that the user needs location permissions in order to play.
            Snackbar.make(
                binding.fragmentSaveReminder,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }
    }

    /*
     *  Uses the Location Client to check the current state of location settings, and gives the user
     *  the opportunity to turn on location services within our app.
     */
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {

        //First, create a LocationRequest, a LocationSettingsRequest Builder
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        //Next, use LocationServices to get the Settings Client and create a val called locationSettingsResponseTask to check the location settings.
        val settingsClient = LocationServices.getSettingsClient(activity!!)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        //Check if the exception is of type ResolvableApiException and if so,
        // try calling the startResolutionForResult() method in order to prompt the user to turn on device location.
        locationSettingsResponseTask.addOnFailureListener { exception ->

            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        activity,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    //If calling startResolutionForResult enters the catch block, print a log.
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                //If the exception is not of type ResolvableApiException,
                // present a snackbar that alerts the user that location needs to be enabled to play the treasure hunt.
                Snackbar.make(
                    binding.fragmentSaveReminder,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }

        //If the locationSettingsResponseTask does complete, check that it is successful, if so you will want to add the geofence.
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                //addGeofenceForClue()
            }
        }
    }

    //After the user chooses whether to accept or deny device location permissions,
    // this checks if the user has chosen to accept the permissions. If not, it will ask again.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndStartGeofence(false)
        }
    }

}

private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1