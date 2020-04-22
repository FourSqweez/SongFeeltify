/*
 * Copyright 2017 Andrius Baruckis www.baruckis.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.songfeelsfinal.songfeels.ui.spotify.models;

import android.os.Parcel;
import android.os.Parcelable;


/*
* Make artist object parcelable as it allows to save and retain the list
* when user rotates the screen.
* */
public class CustomPlaylist implements Parcelable {

    private String id;
    private String name;
    private String imageUrl;
    private int totalSong;

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    private String externalUrl;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getTotalSong() { return totalSong; }

    public void setTotalSong(int totalSong) {
        this.totalSong = totalSong;
    }

    public static final Parcelable.Creator<CustomPlaylist> CREATOR = new Parcelable.Creator<CustomPlaylist>() {
        public CustomPlaylist createFromParcel(Parcel in) {
            CustomPlaylist mCustomPlaylist = new CustomPlaylist();
            mCustomPlaylist.id = in.readString();
            mCustomPlaylist.name = in.readString();
            mCustomPlaylist.imageUrl = in.readString();
            mCustomPlaylist.totalSong = in.readInt();
            return mCustomPlaylist;
        }

        public CustomPlaylist[] newArray(int size) {
            return new CustomPlaylist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(imageUrl);
        out.writeInt(totalSong);
    }


}
