package com.parchapp.dominio;

import com.parchapp.estructuras.Cola;
import com.parchapp.estructuras.ListaEnlazada;
import java.time.LocalDateTime;

// Actividad grupal en un lugar. Reglas:
// - La capacidad incluye a quien crea el parche, que entra como primer participante.
// - Nadie puede estar dos veces, ni como participante ni en la lista de espera.
// i está lleno, quien quiera entrar va a la lista de espera (cola FIFO). Cuando alguien
//  sale, el primero de la cola ocupa su cupo.
// Solo los parches ABIERTO o LLENO aceptan cambios.
public class Parche {

    public static final int CAPACIDAD_MINIMA = 2;
    public static final int CAPACIDAD_MAXIMA = 30;
    public static final int LARGO_MAXIMO_TITULO = 40;

    private final String id;
    private final Lugar lugar;
    private final Usuario creador;
    private final String titulo;
    private final LocalDateTime fechaHora;
    private final int capacidad;
    private final ListaEnlazada<Usuario> participantes = new ListaEnlazada<>();
    private final Cola<Usuario> listaEspera = new Cola<>();
    private EstadoParche estado = EstadoParche.ABIERTO;

    public Parche(String id, Lugar lugar, Usuario creador, String titulo, LocalDateTime fechaHora, int capacidad) {
        if (lugar == null || creador == null || fechaHora == null) {
            throw new IllegalArgumentException("El parche necesita lugar, creador y fecha");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El parche necesita un titulo");
        }
        if (titulo.length() > LARGO_MAXIMO_TITULO) {
            throw new IllegalArgumentException("El titulo puede tener maximo " + LARGO_MAXIMO_TITULO + " caracteres");
        }
        if (capacidad < CAPACIDAD_MINIMA || capacidad > CAPACIDAD_MAXIMA) {
            throw new IllegalArgumentException("La capacidad debe estar entre " + CAPACIDAD_MINIMA
                    + " y " + CAPACIDAD_MAXIMA + " personas");
        }
        this.id = id;
        this.lugar = lugar;
        this.creador = creador;
        this.titulo = titulo.strip();
        this.fechaHora = fechaHora;
        this.capacidad = capacidad;
        participantes.agregarAlFinal(creador);
    }

    public ResultadoUnion unirse(Usuario usuario) {
        if (!estaActivo()) {
            return ResultadoUnion.NO_DISPONIBLE;
        }
        if (participantes.contiene(usuario)) {
            return ResultadoUnion.YA_PARTICIPA;
        }
        if (listaEspera.contiene(usuario)) {
            return ResultadoUnion.YA_EN_ESPERA;
        }
        if (participantes.tamano() < capacidad) {
            participantes.agregarAlFinal(usuario);
            if (participantes.tamano() == capacidad) {
                estado = EstadoParche.LLENO;
            }
            return ResultadoUnion.UNIDO;
        }
        listaEspera.encolar(usuario);
        return ResultadoUnion.EN_LISTA_DE_ESPERA;
    }

    // Devuelve quién pasó de la lista de espera al parche, o null si no había nadie esperando
    public Usuario salir(Usuario usuario) {
        if (!estaActivo()) {
            throw new IllegalArgumentException("El parche ya no esta activo");
        }
        if (usuario.equals(creador)) {
            throw new IllegalArgumentException("Quien crea el parche no puede salirse; puede cancelarlo");
        }
        if (!participantes.eliminar(usuario)) {
            throw new IllegalArgumentException(usuario.getNombre() + " no participa en este parche");
        }
        if (listaEspera.estaVacia()) {
            estado = EstadoParche.ABIERTO;
            return null;
        }
        Usuario siguiente = listaEspera.desencolar();
        participantes.agregarAlFinal(siguiente);
        return siguiente;
    }

    public void cancelar(Usuario usuario) {
        if (!usuario.equals(creador)) {
            throw new IllegalArgumentException("Solo quien creo el parche puede cancelarlo");
        }
        if (!estaActivo()) {
            throw new IllegalArgumentException("El parche ya no esta activo");
        }
        estado = EstadoParche.CANCELADO;
    }

    // Recibe la hora actual como parámetro para poder probarlo con cualquier fecha
    public void finalizarSiYaPaso(LocalDateTime ahora) {
        if (estaActivo() && fechaHora.isBefore(ahora)) {
            estado = EstadoParche.FINALIZADO;
        }
    }

    public boolean estaActivo() {
        return estado == EstadoParche.ABIERTO || estado == EstadoParche.LLENO;
    }

    public boolean esCreador(Usuario usuario) {
        return creador.equals(usuario);
    }

    public boolean esParticipante(Usuario usuario) {
        return participantes.contiene(usuario);
    }

    // 1 = primero en la fila; 0 = no está esperando
    public int posicionEnEspera(Usuario usuario) {
        return listaEspera.posicionDe(usuario);
    }

    public int cuposLibres() {
        return capacidad - participantes.tamano();
    }

    public int cantidadParticipantes() {
        return participantes.tamano();
    }

    public int cantidadEnEspera() {
        return listaEspera.tamano();
    }

    // Se devuelven como Iterable para que desde afuera solo se puedan recorrer, no modificar
    public Iterable<Usuario> getParticipantes() {
        return participantes;
    }

    public Iterable<Usuario> getListaEspera() {
        return listaEspera;
    }

    public boolean estructurasValidas() {
        return participantes.invariantesValidos() && listaEspera.invariantesValidos();
    }

    public String dibujarParticipantes() {
        return participantes.dibujarEnlaces(Usuario::getNombre);
    }

    public String dibujarListaEspera() {
        return listaEspera.dibujarEnlaces(Usuario::getNombre);
    }

    public String getId() {
        return id;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public Usuario getCreador() {
        return creador;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public EstadoParche getEstado() {
        return estado;
    }
}
