import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        final String URL = "jdbc:oracle:thin:@//localhost:1521/xe"; // Cambia según tu BD
        final String USUARIO = "RIBERA";
        final String CONTRASENIA = "ribera";

        boolean terminado = false;

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
                    mostrarDatos(URL,USUARIO,CONTRASENIA);
                    break;
                case 2:
                    System.out.println("Actualizar o insertar datos? (1,2)");
                    int opcion2 = sc.nextInt();
                    sc.nextLine();
                    if (opcion2 == 1) {
                        actualizarDatos(URL,USUARIO,CONTRASENIA);
                        break;
                    }else if (opcion2 == 2) {
                        insertarDatos(URL,USUARIO,CONTRASENIA);
                        break;
                    }
                case 3:
                    elminarDatos(URL,USUARIO,CONTRASENIA);
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    terminado = true;
                    break;
                default:
                    System.out.println("Error: Opcion incorrecta");
                    break;
            }
        }while (!terminado);
        sc.close();

    }

    public static void actualizarDatos(String url, String usuario, String contraseña) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el id del ciclista a actualizar: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.println("Ingrese la edad nueva del ciclista: ");
        int edad = sc.nextInt();
        sc.nextLine();
        System.out.println("Ingrese el id del equipo nuevo del ciclista: ");
        int  equipo = sc.nextInt();
        sc.nextLine();
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()){

            String sql ="UPDATE ciclista SET edad = ? AND id_equipo = ? WHERE id_ciclista = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, edad);
            ps.setInt(2, equipo);
            ps.setInt(3, id);
            ps.executeUpdate();
            System.out.println("Todo bien actualizado");
        } catch (SQLException e) {
            System.out.println("Error al actualizar en tabla: " + e.getMessage());
        }
        sc.close();
    }

    public static void mostrarDatos(String url, String usuario, String contraseña){
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()){

            String sql = "SELECT c.id_ciclista AS id, c.nombre AS nombre, " +
                    "c.nacionalidad AS nacionalidad, c.edad AS edad, " +
                    "e.nombre AS nombre_equipo from ciclista c join equipo e " +
                    "ON c.id_equipo=e.id_equipo";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String equipo = rs.getString("nombre_equipo");
                String ciclista = rs.getString("nombre");
                String nacionalidad = rs.getString("nacionalidad");
                int edad = rs.getInt("edad");
                System.out.println("Datos: ");
                System.out.println("ID del ciclista: " + id);
                System.out.println("Nombre del ciclista: " + ciclista);
                System.out.println("Nombre de la nacionalidad: " + nacionalidad);
                System.out.println("Edad del ciclista: " + edad);
                System.out.println("Nombre del equipo: " + equipo);
            }
        } catch (SQLException e) {
            System.out.println("Error al mostar la tabla: " + e.getMessage());
        }
    }


    public static void insertarDatos(String url, String usuario, String contraseña){
        int id_equipo_numero= 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el nombre del ciclista: ");
        String nombre = sc.nextLine();
        System.out.println("Ingrese la nacionalidad del ciclista: ");
        String nacionalidad = sc.nextLine();
        System.out.println("Ingrese la edad del ciclista: ");
        int edad = sc.nextInt();
        System.out.println("Ingrese el id del equipo: ");
        int id_equipo = sc.nextInt();
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()){
            String sql_id_ciclistas = "SELECT MAX(id_ciclista) FROM ciclista";
            ResultSet rs2 = stmt.executeQuery(sql_id_ciclistas);
            while (rs2.next()) {
                int id = rs2.getInt("id_ciclista");
                id_equipo_numero = id;
            }

            String sql ="INSERT INTO CICLISTA (id_ciclista, nombre, nacionalidad, edad. id_equipo) " +
                    "VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,id_equipo_numero+1);
            ps.setString(2, nombre);
            ps.setString(3, nacionalidad);
            ps.setInt(4, edad);
            ps.setInt(5, id_equipo);
            ps.executeUpdate();


            System.out.println("Todo bien insertado");
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error al insertar en tabla: " + e.getMessage());
        }
        sc.close();
    }

    public static void elminarDatos(String url, String usuario, String contraseña) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese el id del ciclista a eliminar: ");
        int id_equipo = sc.nextInt();
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()){

            String sql_participacion ="DELETE FROM participacion WHERE id_equipo = ?";
            PreparedStatement ps = conn.prepareStatement(sql_participacion);
            ps.setInt(1,id_equipo);

            String sql_ciclista ="DELETE FROM ciclista WHERE id_ciclista = ?";
            PreparedStatement ps2 = conn.prepareStatement(sql_ciclista);
            ps2.setInt(1,id_equipo);
            ps2.executeUpdate();

            System.out.println("Todo bien eliminado");
            ps.close();
            ps2.close();
        } catch (SQLException e) {
            System.out.println("Error al elimnar en tabla: " + e.getMessage());
        }
        sc.close();
    }
}