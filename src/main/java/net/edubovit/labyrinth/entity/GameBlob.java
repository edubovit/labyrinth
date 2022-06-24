package net.edubovit.labyrinth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Table("game")
public class GameBlob {

    @Id
    private UUID id;

    private byte[] gameBlob;

    private LocalDateTime lastUpdate;

}
