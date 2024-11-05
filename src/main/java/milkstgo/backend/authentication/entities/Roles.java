package milkstgo.backend.authentication.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Roles {
    ADMIN("ADMINISTRADOR"),
    USER("USUARIO");

    @Getter
    private final String rol;
}
