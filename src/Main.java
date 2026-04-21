// Importes necesarios para el programa
import java.sql.*;
import java.util.Scanner;

// Clase Main para la ejecucion del programa
public class Main {
    public static void main(String[] args) {
        // Creacion de un Scanner para leer opciones del usuario
        Scanner sc = new Scanner(System.in);
        
        // Datos para conectarse a la base de datos (constantes con final)
        final String URL = "jdbc:oracle:thin:@//localhost:1521/xe"; // Cambia según tu BD
        final String USUARIO = "RIBERA";
        final String CONTRASENIA = "ribera";
        
        // Variable de control para el bucle
        boolean terminado = false;
        
        // Bucle do-while que muestra el menú principal
        do {
            System.out.println("----MENU----");
            System.out.println("1---MOSTRAR DATOS");
            System.out.println("2---ACTUALIZAR DATOS");
            System.out.println("3---ELIMINAR DATOS");
            System.out.println("4---SALIR");
            System.out.println("-------------");
            System.out.println("Ingrese la opcion: ");
            int opcion = sc.nextInt();
            sc.nextLine();
            
            switch (opcion) {
                case 1:
                    mostrarDatos(URL, USUARIO, CONTRASENIA);
                    break;
                case 2:
                    System.out.println("Actualizar o insertar datos? (1,2)");
                    int opcion2 = sc.nextInt();
                    sc.nextLine();
                    if (opcion2 == 1) {
                        actualizarDatos(sc, URL, USUARIO, CONTRASENIA);
                    } else if (opcion2 == 2) {
                        insertarDatos(sc, URL, USUARIO, CONTRASENIA);
                    } else {
                        System.out.println("Opción no válida");
                    }
                    break;
                case 3:
                    eliminarDatos(sc, URL, USUARIO, CONTRASENIA); 
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    terminado = true;
                    break;
                default:
                    System.out.println("Error: Opcion incorrecta");
                    break;
            }
        } while (!terminado);
        sc.close();
    }
    
    // ==================== MÉTODO ACTUALIZAR DATOS ====================
    public static void actualizarDatos(Scanner sc, String url, String usuario, String contraseña) {
        System.out.println("Ingrese el id del ciclista a actualizar: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.println("Ingrese la edad nueva del ciclista: ");
        int edad = sc.nextInt();
        sc.nextLine();
        System.out.println("Ingrese el id del equipo nuevo del ciclista: ");
        int equipo = sc.nextInt();
        sc.nextLine();
        
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            
            // Verificar que el equipo existe
            String checkEquipo = "SELECT id_equipo FROM equipo WHERE id_equipo = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkEquipo)) {
                checkPs.setInt(1, equipo);
                if (!checkPs.executeQuery().next()) {
                    System.out.println("ERROR: El equipo con ID " + equipo + " no existe");
                    return;
                }
            }
            
            // Verificar que el ciclista existe
            String checkCiclista = "SELECT id_ciclista FROM ciclista WHERE id_ciclista = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkCiclista)) {
                checkPs.setInt(1, id);
                if (!checkPs.executeQuery().next()) {
                    System.out.println("ERROR: El ciclista con ID " + id + " no existe");
                    return;
                }
            }
            
            String sql = "UPDATE ciclista SET edad = ?, id_equipo = ? WHERE id_ciclista = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, edad);
                ps.setInt(2, equipo);
                ps.setInt(3, id);
                int filas = ps.executeUpdate();
                
                if (filas > 0) {
                    System.out.println("Todo bien actualizado");
                } else {
                    System.out.println("No se encontró el ciclista con ID: " + id);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar en tabla: " + e.getMessage());
        }
    }
    
    // ==================== MÉTODO MOSTRAR DATOS ====================
    public static void mostrarDatos(String url, String usuario, String contraseña) {
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT c.id_ciclista AS id, c.nombre AS nombre, " +
                 "c.nacionalidad AS nacionalidad, c.edad AS edad, " +
                 "e.nombre AS nombre_equipo FROM ciclista c JOIN equipo e " +
                 "ON c.id_equipo = e.id_equipo")) {
            
            System.out.println("\n--- LISTADO DE CICLISTAS ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Nacionalidad: " + rs.getString("nacionalidad"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Equipo: " + rs.getString("nombre_equipo"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error al mostrar la tabla: " + e.getMessage());
        }
    }
    
    // ==================== MÉTODO INSERTAR DATOS ====================
    public static void insertarDatos(Scanner sc, String url, String usuario, String contraseña) {
        System.out.println("Ingrese el nombre del ciclista: ");
        String nombre = sc.nextLine();
        System.out.println("Ingrese la nacionalidad del ciclista: ");
        String nacionalidad = sc.nextLine();
        System.out.println("Ingrese la edad del ciclista: ");
        int edad = sc.nextInt();
        sc.nextLine();
        System.out.println("Ingrese el id del equipo: ");
        int id_equipo = sc.nextInt();
        sc.nextLine();
        
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            
            // Verificar que el equipo existe
            String checkEquipo = "SELECT id_equipo FROM equipo WHERE id_equipo = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkEquipo)) {
                checkPs.setInt(1, id_equipo);
                if (!checkPs.executeQuery().next()) {
                    System.out.println("ERROR: El equipo con ID " + id_equipo + " no existe");
                    return;
                }
            }
            
            // Obtener el siguiente ID disponible
            String sql_id = "SELECT MAX(id_ciclista) AS nuevo_id FROM ciclista";
            int nuevo_id = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql_id)) {
                if (rs.next()) {
                    nuevoId = rs.getInt("nuevo_id");
                }
            }
            
            String sql = "INSERT INTO CICLISTA (id_ciclista, nombre, nacionalidad, edad, id_equipo) VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, nuevoId);
                ps.setString(2, nombre);
                ps.setString(3, nacionalidad);
                ps.setInt(4, edad);
                ps.setInt(5, id_equipo);
                ps.executeUpdate();
                System.out.println("Todo bien insertado. ID asignado: " + nuevoId);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al insertar en tabla: " + e.getMessage());
        }
    }
    
    // ==================== MÉTODO ELIMINAR DATOS ====================
    public static void eliminarDatos(Scanner sc, String url, String usuario, String contraseña) {
        System.out.println("Ingrese el id del ciclista a eliminar: ");
        int id_ciclista = sc.nextInt();
        sc.nextLine();
        
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            conn.setAutoCommit(false); // Transacción para asegurar consistencia
            
            // Verificar que el ciclista existe
            String checkCiclista = "SELECT id_ciclista FROM ciclista WHERE id_ciclista = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkCiclista)) {
                checkPs.setInt(1, id_ciclista);
                if (!checkPs.executeQuery().next()) {
                    System.out.println("ERROR: El ciclista con ID " + id_ciclista + " no existe");
                    conn.rollback();
                    return;
                }
            }
            
            // Primero eliminar participaciones (si existen)
            String sql_participacion = "DELETE FROM participacion WHERE id_ciclista = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql_participacion)) {
                ps.setInt(1, id_ciclista);
                int participacionesEliminadas = ps.executeUpdate();
                if (participacionesEliminadas > 0) {
                    System.out.println("Se eliminaron " + participacionesEliminadas + " participaciones asociadas");
                }
            }
            
            // Luego eliminar el ciclista
            String sql_ciclista = "DELETE FROM ciclista WHERE id_ciclista = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql_ciclista)) {
                ps.setInt(1, id_ciclista);
                int filas = ps.executeUpdate();
                
                if (filas > 0) {
                    conn.commit();
                    System.out.println("Ciclista eliminado correctamente");
                } else {
                    System.out.println("No se encontró el ciclista con ID: " + id_ciclista);
                    conn.rollback();
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error al eliminar en tabla: " + e.getMessage());
        }
    }
}
