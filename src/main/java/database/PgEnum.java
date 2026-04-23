package database;

/**
 * Wrapper para pasar valores de tipos ENUM personalizados de PostgreSQL
 * a través de JDBC. El driver JDBC no convierte String -> ENUM automáticamente,
 * pero sí acepta un PGobject con el tipo y valor explícitos.
 *
 * Uso en un repositorio:
 *   new PgEnum("estado_reserva", r.getEstado())
 *   new PgEnum("rol_usuario",    u.getRol())
 */
public class PgEnum {

    private final String pgType;
    private final String value;

    public PgEnum(String pgType, String value) {
        this.pgType = pgType;
        this.value  = value;
    }

    public String getPgType() { return pgType; }
    public String getValue()  { return value;  }

    @Override
    public String toString() {
        return value + "::" + pgType;
    }
}
