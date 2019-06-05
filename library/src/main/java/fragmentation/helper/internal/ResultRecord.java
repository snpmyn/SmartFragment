package fragmentation.helper.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @decs: ResultRecord
 * @author: 郑少鹏
 * @date: 2019/5/20 9:33
 */
public final class ResultRecord implements Parcelable {
    public static final Creator<ResultRecord> CREATOR = new Creator<ResultRecord>() {
        @Override
        public ResultRecord createFromParcel(Parcel in) {
            return new ResultRecord(in);
        }

        @Override
        public ResultRecord[] newArray(int size) {
            return new ResultRecord[size];
        }
    };
    public int requestCode;
    public int resultCode = 0;
    public Bundle resultBundle;

    public ResultRecord() {

    }

    private ResultRecord(Parcel in) {
        requestCode = in.readInt();
        resultCode = in.readInt();
        resultBundle = in.readBundle(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(requestCode);
        dest.writeInt(resultCode);
        dest.writeBundle(resultBundle);
    }
}
