package org.harsh.tuple.paisa.model;


import lombok.Data;


@Data
public class EmailDetails {
    private final String email;
    private final String subject;
    private final String body;
}
