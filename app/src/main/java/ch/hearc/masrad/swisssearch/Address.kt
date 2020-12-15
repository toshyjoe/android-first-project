package ch.hearc.masrad.swisssearch

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Address (
    var id: Int,
    var name: String?,
    var street: String?,
    var streetNo: String?,
    var phoneNumber: String?,
    var zip: String?,
    var city: String?
) : Parcelable