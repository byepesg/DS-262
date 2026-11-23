package bd;

public class Usuario implements Comparable<Usuario> {
    private int id;
    private String username;
    private String passwordHash;
    private Rol rol;


    public Usuario(int id, String username, String passwordHash, Rol rol) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    @Override
    public int compareTo(Usuario otro) {
        return Integer.compare(id, otro.getId()); // = id-otro.id
    }
}