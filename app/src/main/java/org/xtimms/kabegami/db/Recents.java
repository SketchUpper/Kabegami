package org.xtimms.kabegami.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "recents", primaryKeys = {"imageLink", "categoryId"})
public class Recents {

    @ColumnInfo(name = "imageLink")
    @NonNull
    private String imageLink;

    @ColumnInfo(name = "categoryId")
    @NonNull
    private String categoryId;

    @ColumnInfo(name = "saveTime")
    @NonNull
    private String saveTime;

    @ColumnInfo(name = "key")
    @NonNull
    private String key;

    public Recents(@NonNull String imageLink, @NonNull String categoryId, @NonNull String saveTime, @NonNull String key) {
        this.imageLink = imageLink;
        this.categoryId = categoryId;
        this.saveTime = saveTime;
        this.key = key;
    }

    @NonNull
    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(@NonNull String imageLink) {
        this.imageLink = imageLink;
    }

    @NonNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(@NonNull String saveTime) {
        this.saveTime = saveTime;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }
}
