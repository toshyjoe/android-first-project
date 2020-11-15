package ch.hearc.masrad.swisssearch

class Address {

    var id: Int? = null
    var name: String? = null
    var phoneNumber: String? = null

    constructor(id: Int, name: String, phoneNumber: String) {
        this.id = id
        this.name = name
        this.phoneNumber = phoneNumber

    }
}