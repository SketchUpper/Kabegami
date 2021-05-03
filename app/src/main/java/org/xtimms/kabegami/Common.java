package org.xtimms.kabegami;

import org.xtimms.kabegami.model.WallpaperItem;
import org.xtimms.kabegami.remote.IComputerVision;
import org.xtimms.kabegami.remote.RetrofitClient;

public class Common {

    public static final String STR_CATEGORY_BACKGROUND = "CategoryBackground";
    public static final String STR_WALLPAPER = "Backgrounds";
    public static String STR_CATEGORY_ID_SELECTED;
    public static String STR_CATEGORY_SELECTED;

    public static final int PERMISSION_REQUEST_CODE = 101;
    public static final int SIGN_IN_REQUEST_CODE = 102;
    public static final int PICK_IMAGE_REQUEST = 103;

    public static WallpaperItem selectBackground = new WallpaperItem();
    public static String selectBackgroundKey;

    public static String BASE_URL = "https://westeurope.api.cognitive.microsoft.com/vision/v3.0/";
    public static IComputerVision getComputerVisionAPI() {
        return RetrofitClient.getClient(BASE_URL).create(IComputerVision.class);
    }
    public static String getAPIAdultEndPoint() {
        return new StringBuilder(BASE_URL).append("analyze?visualFeatures=Adult&language=en").toString();
    }
}
