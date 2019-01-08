package com.dreampany.frame.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Hawladar Roman on 30/4/18.
 * Dreampany
 * dreampanymail@gmail.com
 */
public abstract class Task<T extends Parcelable> extends Base {
    
    protected String comment;
    protected T input;

    protected Task() {
    }

    protected Task(Parcel in) {
        super(in);
        comment = in.readString();
        if (in.readByte() == 0) {
            input = null;
        } else {
            Class<?> itemClazz = (Class<?>) in.readSerializable();
            input = in.readParcelable(itemClazz.getClassLoader());
        }
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(comment);
        if (input == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            Class itemClazz = input.getClass();
            dest.writeSerializable(itemClazz);
            dest.writeParcelable(input, flags);
        }
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setInput(T input) {
        this.input = input;
    }

    public String getComment() {
        return comment;
    }

    public T getInput() {
        return input;
    }

}
