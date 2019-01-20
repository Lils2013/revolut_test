package tsoy.alexander.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

public class TransferDeserializer extends JsonDeserializer<Transfer> {

    @Override
    public Transfer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        final Long sourceAccountId = node.get("sourceAccountId").asLong();
        final Long destinationAccountId = node.get("destinationAccountId").asLong();
        final String amount = node.get("amount").asText();
        final String currency = node.get("currency").asText();
        return new Transfer(sourceAccountId, destinationAccountId, new BigDecimal(amount), Currency.getInstance(currency));
    }
}
