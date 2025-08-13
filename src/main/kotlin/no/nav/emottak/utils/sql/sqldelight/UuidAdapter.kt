package no.nav.emottak.utils.sql.sqldelight

import app.cash.sqldelight.ColumnAdapter
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

public data object UuidAdapter : ColumnAdapter<Uuid, UUID> {
    override fun decode(databaseValue: UUID): Uuid = databaseValue.toKotlinUuid()
    override fun encode(value: Uuid): UUID = value.toJavaUuid()
}
