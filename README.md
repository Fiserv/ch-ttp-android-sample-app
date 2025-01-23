# Sample App - Fiserv Tap to Pay for Android 

# Purpose
This sample app demonstrates the capabilities of Fiserv's Tap to Pay on Android functionality, leveraging Fiserv's Tap to Pay Android SDK.​

# Background
You should first read through and understand the steps for obtaining API credentials, etc. that are required for Fiserv's implementation of Tap To Pay. The Fiserv [Tap to Pay on Android SDK integration guide] (https://developer.fiserv.com/product/CommerceHub/docs/?path=docs/In-Person/Integrations/Mobile-SDK/Android-TTP.md&branch=preview) has all the info you need.​

# Setup
​You will need to make a few changes to the sample app to get it to work for you.​

By following the steps in the Tap to Pay on Android SDK integration guide, you should have obtained test API credentials and a test Merchant ID (MID). You need to copy and paste these values in /app/build.gradle. 
 `buildConfigField "String", PPID, "\"${FLAVOUR_VALUE_CERT}.${PPID_VALUE}${PPID_COUNTER_CERT_VALUE}\""
  buildConfigField "String", API_KEY, "\"${API_KEY_CERT_VALUE}\""
  buildConfigField "String", API_SECRET, "\"${API_SECRET_CERT_VALUE}\""
  buildConfigField "String", MID, "\"${MID_CERT_VALUE}\""
  buildConfigField "String", TID, "\"${TID_CERT_VALUE}\""
  buildConfigField "String", HOST_PORT, "\"${HOST_PORT_VALUE}\""`
