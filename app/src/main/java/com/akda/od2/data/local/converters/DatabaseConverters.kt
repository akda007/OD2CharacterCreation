package com.akda.od2.data.local.converters

import androidx.room.TypeConverter
import com.akda.od2.domain.model.Alignment
import com.akda.od2.domain.model.Attribute
import com.akda.od2.domain.model.AttributeType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromAlignment(alignment: Alignment?): String? {
        return alignment?.name
    }

    @TypeConverter
    fun toAlignment(name: String?): Alignment? {
        return name?.let { Alignment.valueOf(it) }
    }

    private val attrMapType =
        object : TypeToken<Map<AttributeType, Attribute>>() {}.type

    @TypeConverter
    fun fromAttributeMap(map: Map<AttributeType, Attribute>?): String {
        return gson.toJson(map, attrMapType)
    }

    @TypeConverter
    fun toAttributeMap(json: String?): Map<AttributeType, Attribute> {
        return gson.fromJson(json, attrMapType) ?: emptyMap()
    }
}