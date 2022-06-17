package net.edubovit.labyrinth.dto;

import javax.validation.constraints.NotBlank;

public record UserCredentialsDTO(@NotBlank String username,
                                 @NotBlank String password) {

}
