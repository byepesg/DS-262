package controlador;

import modelo.estructuras.AVL;
import modelo.estructuras.Cola;
import bd.Permiso;
import bd.Rol;
import bd.Usuario;
import bd.UsuariosBD;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Scanner;

public class LoginControlador {
    private AVL<Usuario> arbolUsuarios;
    private Cola<Long> intentosFallidos;
    private UsuariosBD usuariosBD;
    private Usuario usuarioActual;

    //Scanner para las lecturas
    Scanner entrada = new Scanner(System.in);
    String escritura;


    public void iniciar(){
        //Inicialización de atributos
        this.usuariosBD = new UsuariosBD();
        this.intentosFallidos = new Cola<>();


        System.out.println("Bienvenido al sistema de login :)");
        arbolUsuarios = usuariosBD.cargarUsuariosEnAVL();

        if(arbolUsuarios.estaVacio()){ //no hay usuarios guardados
            System.out.println("Detectamos que es su primera vez usando este sistema, agregaremos al primer administrador.");
            int id =0;
            String usuario="";
            String password="";


            boolean verificacionID = true;
            while(verificacionID){
                System.out.println("Escriba el ID (numérico):");
                escritura = entrada.nextLine();
                try {
                    id = Integer.parseInt(escritura);
                } catch (NumberFormatException e) {
                    System.out.println("Error, ingrese un ID numérico válido.");
                    continue;
                }
                System.out.println("Es correcto el ID "+id+" ?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    if(Integer.parseInt(escritura)==1){
                        verificacionID =false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
                
            }

            boolean verificacionContrasegna = true;
            while(verificacionContrasegna){
                System.out.println("Digite la contraseña: ");
                escritura = entrada.nextLine();
                password = escritura;
                System.out.println("Vuelva a digitar la contraseña: ");
                escritura = entrada.nextLine();
                if(password.equals(escritura)){
                    System.out.println("Contraseña ingresada con éxito!!! ");
                    verificacionContrasegna =false;
                }else{
                    System.out.println("Las contraseñas no coinciden, vuelva a ingresar su contraseña.");
                }
            }

            boolean verificacionUsuario = true;
            while(verificacionUsuario){
                System.out.println("Digite el nombre del usuario:");
                escritura = entrada.nextLine();
                usuario = escritura;
                System.out.println("Es correcto el usuario: "+usuario+" ?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    if(Integer.parseInt(escritura)==1){
                        verificacionUsuario =false;
                        
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }

            int idPermiso = usuariosBD.guardarPermiso("consola");
            int idRol = usuariosBD.guardarRol("Administrador");
            usuariosBD.asignarPermisoARol(idRol, idPermiso);

            Rol rolAdmin = new Rol(idRol, "Administrador");
            rolAdmin.agregarPermiso(new Permiso(idPermiso, "consola"));

            usuarioActual = new Usuario(id, usuario, generarHash(password), rolAdmin);
            if (usuariosBD.guardarUsuario(usuarioActual)) {
                arbolUsuarios.insertar(usuarioActual);
            } else {
                System.out.println("Error: no se pudo registrar el administrador en la base de datos.");
            }
            
        }else{ //Ya hay usuarios guardados!
            Usuario usuarioSupuesto = null;

            boolean verificacionID = true;
            while(verificacionID){
                System.out.println("Ingrese su ID (numérico): "); //ID podría ser el numero de documento
                escritura = entrada.nextLine();
                try {
                    int idBuscado = Integer.parseInt(escritura);
                    usuarioSupuesto = arbolUsuarios.buscar(new Usuario(idBuscado, null, null, null));

                    if (usuarioSupuesto == null) {
                        System.out.println("ID no encontrado");
                    } else {
                        verificacionID = false;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error, ingrese un ID numérico válido.");
                }
            }

            boolean verificacionPassword=true;
            while(verificacionPassword==true){
                System.out.println("Ingrese su contraseña: ");
                escritura = entrada.nextLine();
                if(usuarioSupuesto.getPasswordHash().equals(generarHash(escritura))){
                    verificacionPassword=false;
                }else{
                    System.out.println("Eror, contraseña incorrecta");
                }
            }
            
            usuarioActual = usuarioSupuesto;
        }

        if(usuarioActual.getRol().tienePermiso("consola")){ //el usuario puede acceder al menu de consola
            menuConsola();
        }
    
    }

    public boolean autenticar(String username, String password){
        return false;
    }

    public boolean estaBloqueado(String username){
        return false;
    }

    public Usuario getUsuarioActual(){
        return usuarioActual;
    }

    public static String generarHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash SHA-256", e);
        }
    }

    public void agregarUsuario(){
        int id = 0;
        String usuario = "";
        String password = "";
        int rol = 0;
        Rol rolSeleccionado = null;
        ArrayList<Rol> roles = usuariosBD.obtenerRoles();

        if (roles.isEmpty()) {
            System.out.println("No hay roles disponibles. Cree un rol antes de agregar usuarios.");
            return;
        }

        boolean verificacionID = true;
        while(verificacionID){
            System.out.println("Escriba el ID (numérico):");
            escritura = entrada.nextLine();
            try {
                id = Integer.parseInt(escritura);
            } catch (NumberFormatException e) {
                System.out.println("Error, ingrese un ID numérico válido.");
                continue;
            }
            while(true){
                System.out.println("Es correcto el ID " + id + " ?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 1){
                        verificacionID = false;
                        break;
                    }
                    else if(opcion == 2){
                        break;
                    }
                    else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }
        }

        boolean verificacionContrasegna = true;
        while(verificacionContrasegna){
            System.out.println("Digite la contraseña: ");
            escritura = entrada.nextLine();
            password = escritura;
            System.out.println("Vuelva a digitar la contraseña: ");
            escritura = entrada.nextLine();
            if(password.equals(escritura)){
                System.out.println("Contraseña ingresada con éxito!!! ");
                verificacionContrasegna = false;
            }else{
                System.out.println("Las contraseñas no coinciden, vuelva a ingresar su contraseña.");
            }
        }

        boolean verificacionUsuario = true;
        while(verificacionUsuario){
            System.out.println("Digite el nombre del usuario:");
            escritura = entrada.nextLine();
            usuario = escritura;
            while(true){
                System.out.println("Es correcto el usuario: " + usuario + " ?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 1){
                        verificacionUsuario = false;
                        break;
                    }
                    else if(opcion == 2){
                        break;
                    }else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }
        }

        boolean verificacionRol = true;
        while(verificacionRol){
            System.out.println("Digite el id del rol:");
            escritura = entrada.nextLine();
            try {
                rol = Integer.parseInt(escritura);
            } catch (NumberFormatException e) {
                System.out.println("Error, ingrese un ID numérico válido.");
                continue;
            }

            rolSeleccionado = null;
            for (Rol r : roles) {
                if (r.getId() == rol) {
                    rolSeleccionado = r;
                    break;
                }
            }

            if(rolSeleccionado == null){
                System.out.println("El rol no existe.");
                continue;
            }

            while(true){
                System.out.println("Es correcto el rol " + rolSeleccionado.getNombreRol() + " con id " + rolSeleccionado.getId() + " ?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 1){
                        verificacionRol = false;
                        break;
                    }
                    else if(opcion == 2){
                        break;
                    }else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }
        }
        usuariosBD.guardarUsuario(new Usuario(id, usuario, generarHash(password), rolSeleccionado));
        
        System.out.println("Usuario agregado con éxito!");
    }

    public void agregarRol(){
        int rolId = 0;
        String rolNombre = "";
        
        boolean verificacionNombre = true;
        while(verificacionNombre){
            System.out.println("Ingrese el nombre del rol: ");
            escritura = entrada.nextLine();
            rolNombre = escritura;
            while(true){
                System.out.println("Es correcto el nombre " + rolNombre + "?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 1){
                        verificacionNombre = false;
                        break;
                    }
                    else if(opcion == 2){
                        break;
                    }else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }
        }

        rolId = usuariosBD.guardarRol(rolNombre);

        boolean verificacionPermiso = true;
        while(true){
            System.out.println("Desea agregar un permiso al rol " + rolNombre + "?");
            System.out.println("1.Sí");
            System.out.println("2.No");
            escritura = entrada.nextLine();
            try {
                int opcion = Integer.parseInt(escritura);
                if(opcion == 1){
                    break;
                }
                else if(opcion == 2){
                    verificacionPermiso = false;
                    break;
                }else{
                    System.out.println("No ingresó un número válido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("No ingresó un número válido.");
            } 
        }
        
        while(verificacionPermiso){
            int permisoIdTemp = 0;

            System.out.println("Ingrese el ID del permiso para asignarle al rol: ");
            escritura = entrada.nextLine();
            try {
                permisoIdTemp = Integer.parseInt(escritura);
            } catch (NumberFormatException e) {
                System.out.println("Error, ingrese un ID numérico válido.");
                continue;
            }

            boolean confirmado = false;
            while(true){
                System.out.println("Es correcto el id del permiso " + permisoIdTemp + "?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 1){
                        usuariosBD.asignarPermisoARol(rolId, permisoIdTemp);
                        confirmado = true;
                        break;
                    }
                    else if(opcion == 2){
                        break;
                    }else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }

            if(!confirmado){
                continue;
            }

            while(true){
                System.out.println("Desea agregar otro permiso al rol " + rolNombre + "?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 2){
                        verificacionPermiso = false;
                        break;
                    }
                    else if(opcion == 1){
                        break;
                    }
                    else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                } 
            }
        }
    }

    public void agregarPermiso(){
        String permiso = "";
        boolean verificacionPermiso = true;

        while(verificacionPermiso){
            System.out.println("Digite el nombre del permiso a añadir");
            escritura = entrada.nextLine();
            permiso = escritura;
            
            boolean confirmado = false;
            while(true){
                System.out.println("Es correcto el nombre " + permiso + "?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 1){
                        usuariosBD.guardarPermiso(permiso);
                        confirmado = true;
                        break;
                    }
                    else if(opcion == 2){
                        break;
                    }else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                }
            }

            if(!confirmado){
                continue;
            }

            while(true){
                System.out.println("Desea agregar otro permiso?");
                System.out.println("1.Sí");
                System.out.println("2.No");
                escritura = entrada.nextLine();
                try {
                    int opcion = Integer.parseInt(escritura);
                    if(opcion == 2){
                        verificacionPermiso = false;
                        break;
                    }
                    else if(opcion == 1){
                        break;
                    }
                    else{
                        System.out.println("No ingresó un número válido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("No ingresó un número válido.");
                } 
            }
        }
    }

    public void menuConsola(){
        int opcion=1;
        int ultimaOpcion =4;

        while(opcion>=1 && opcion<=ultimaOpcion){
            System.out.println("Qué desea hacer?");
            System.out.println("1.Agregar usuario");
            System.out.println("2.Agregar permisos");
            System.out.println("3.Agregar rol");
            System.out.println("4.Continuar al programa");

            escritura = entrada.nextLine();
            try {
                opcion = Integer.parseInt(escritura);
                if(opcion<1 || opcion>ultimaOpcion){
                    System.out.println("No ingresó un número válido.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("No ingresó un número válido.");
                continue;
            } 

            switch(opcion){
                case 1:
                    agregarUsuario();
                    break;
                case 2:
                    agregarPermiso();
                    break;
                case 3:
                    agregarRol();
                    break;
                case 4:
                    opcion= 12345;
                    break;
            }
        }
    }

}
