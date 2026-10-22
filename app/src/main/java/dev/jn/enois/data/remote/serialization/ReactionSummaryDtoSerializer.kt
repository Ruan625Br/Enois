package dev.jn.enois.data.remote.serialization


import dev.jn.enois.data.remote.dto.reaction.ReactionSummaryDto
import dev.jn.enois.domain.model.ReactionType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

object ReactionSummaryDtoSerializer : KSerializer<ReactionSummaryDto> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ReactionSummary")

    override fun deserialize(decoder: Decoder): ReactionSummaryDto {
        val input = (decoder as? JsonDecoder)
            ?.decodeJsonElement() as? JsonObject
            ?: error("Expected a JSON object for ReactionSummary")

        val counts = input.mapNotNull { (key, value) ->
            val type = ReactionType.entries
                .firstOrNull { it.name == key }

            val count = value.jsonPrimitive.intOrNull

            if (type != null && count != null) {
                type to count
            } else {
                null
            }
        }.toMap()

        return ReactionSummaryDto(counts = counts)
    }

    override fun serialize(encoder: Encoder, value: ReactionSummaryDto) {
        val output = JsonObject(
            value.counts.mapKeys { (type, _) -> type.name }
                .mapValues { (_, count) ->
                    kotlinx.serialization.json.JsonPrimitive(count)
                }
        )

        (encoder as? kotlinx.serialization.json.JsonEncoder)
            ?.encodeJsonElement(output)
            ?: error("ReactionSummary requires a JSON encoder")
    }
}