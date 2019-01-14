package rose.michaeap.nostresswishlist

import android.os.Parcel
import android.os.Parcelable

data class Item(var name:String,var price:Int,var priority:Boolean,var mult:Boolean,var online:Boolean,var Comments:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(price)
        parcel.writeByte(if (priority) 1 else 0)
        parcel.writeByte(if (mult) 1 else 0)
        parcel.writeByte(if (online) 1 else 0)
        parcel.writeString(Comments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}