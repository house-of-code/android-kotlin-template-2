package io.houseofcode.template2.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Transform LocalDateTime to Date.
 */
fun LocalDateTime.toDate(): Date =
    Date.from(
        this.atZone(ZoneId.systemDefault()).toInstant()
    )