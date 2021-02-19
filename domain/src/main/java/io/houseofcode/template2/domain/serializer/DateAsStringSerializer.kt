package io.houseofcode.template2.domain.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Custom serializer for Kotlin serialization.
 */
object DateAsStringSerializer: KSerializer<Date> {

    private const val DATE_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    @ExperimentalSerializationApi
    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(
            SimpleDateFormat(DATE_FORMAT, Locale.UK).apply {
                timeZone = TimeZone.getTimeZone("GMT")
            }.format(value)
        )
    }

    override fun deserialize(decoder: Decoder): Date {
        return SimpleDateFormat(DATE_FORMAT, Locale.UK).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }.parse(decoder.decodeString()) ?: Date()
    }
}