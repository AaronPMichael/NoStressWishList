package rose.michaeap.nostresswishlist

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Item(var name:String="",var price:Double=0.0,var priority:Boolean=false,var mult:Boolean=false,var online:Boolean=false,var comments:String="",var id:String="",var ownerName:String=""):Parcelable,Serializable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),parcel.readString(),parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeByte(if (priority) 1 else 0)
        parcel.writeByte(if (mult) 1 else 0)
        parcel.writeByte(if (online) 1 else 0)
        parcel.writeString(comments)
        parcel.writeString(id)
        parcel.writeString(ownerName)
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