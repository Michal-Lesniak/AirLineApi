package org.example.airlineapi.model.ticket.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTicketPersonCommand {
    @NotNull(message = "NOT_NULL")
    long personId;
}
