package org.xtimms.kabegami.remote;

import org.xtimms.kabegami.model.vision.ComputerVision;
import org.xtimms.kabegami.model.vision.URLUpload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IComputerVision {

    @Headers({
            "Content-Type: application/json",
            "Ocp-Apim-Subscription-Key: TOKEN"
    })

    @POST
    Call<ComputerVision> analyzeImage(@Url String apiEndPoint, @Body URLUpload url);

}
