package org.example.airlineapi.model.ticket.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateTicketPersonCommand {
    long personId;
}
