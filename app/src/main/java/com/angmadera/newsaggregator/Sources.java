package com.angmadera.newsaggregator;

import android.os.Parcel;
import android.os.Parcelable;

public class Sources implements Parcelable {

    private final String id;
    private final String name;
    private final String topic;
    private final String language;
    private final String country;

    Sources(String id, String name, String topic, String language, String country) {
        this.id = id;
        this.name = name;
        this.topic = topic;
        this.language = language;
        this.country = country;
    }


    protected Sources(Parcel in) {
        id = in.readString();
        name = in.readString();
        topic = in.readString();
        language = in.readString();
        country = in.readString();
    }

    public static final Creator<Sources> CREATOR = new Creator<Sources>() {
        @Override
        public Sources createFromParcel(Parcel in) {
            return new Sources(in);
        }

        @Override
        public Sources[] newArray(int size) {
            return new Sources[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() { return country; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(topic);
        dest.writeString(language);
        dest.writeString(country);
    }
}
