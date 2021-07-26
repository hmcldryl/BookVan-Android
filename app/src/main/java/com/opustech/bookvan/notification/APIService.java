package com.opustech.bookvan.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=\tAAAAI2f8JLk:APA91bFyEdvYU9L_L6NglBFyHrrFAt_8D2qZTAooNNgN8GUdXhQCIc18KFE8Y6DcCPuxte0jV_M5YMVEaLU073gGSsFP04NdJzleazbhkjXg9n6NWQtSGaQKuKPOkZcsCKXIS3qVLBEd"
            }
    )

    @POST("fcm/send")
    Call<RequestResponse> sendNotification(@Body NotificationSender body);
}
