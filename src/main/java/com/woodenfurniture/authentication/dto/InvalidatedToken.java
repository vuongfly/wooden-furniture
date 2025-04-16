package com.woodenfurniture.authentication.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
/*
* this class is used to store invalidated tokens
* TODO: need to run a scheduler to delete expired tokens
* */
public class InvalidatedToken {
    @Id
    String id;
    Date expiryTime;
}
