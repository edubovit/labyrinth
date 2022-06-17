package net.edubovit.labyrinth.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@Builder
public class User {

    @Id
    private UUID id;

    private String username;

    @ToString.Exclude
    private String password;

    private UUID gameId;

}
