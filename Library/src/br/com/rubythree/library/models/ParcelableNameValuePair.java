package br.com.rubythree.library.models;

import org.apache.http.NameValuePair;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableNameValuePair implements NameValuePair, Parcelable {
	String name, value;

	public ParcelableNameValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);
		out.writeString(value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	public static final Parcelable.Creator<ParcelableNameValuePair> CREATOR = new Parcelable.Creator<ParcelableNameValuePair>() {
		public ParcelableNameValuePair createFromParcel (Parcel in) {
			return new ParcelableNameValuePair(in);
		}

		@Override
		public ParcelableNameValuePair[] newArray(int size) {
			return new ParcelableNameValuePair[size];
		}
	};
	
	private ParcelableNameValuePair(Parcel in) {
		name = in.readString();
		value = in.readString();
	}
}