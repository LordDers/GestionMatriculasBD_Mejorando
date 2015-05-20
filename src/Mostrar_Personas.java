

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Alumno;

/**
 * Servlet implementation class Mostrar_Personas
 */
public class Mostrar_Personas extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mostrar_Personas() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		
		Connection con = null;	
		Statement sentencia = null;

		try {
			System.out.println("Empieza mostrando");

			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);

			sentencia = con.createStatement();

			String sql;		        
			sql="SELECT * FROM persona";
			//sql="SELECT persona.dni, persona.nombre, persona.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM persona INNER JOIN alumnos ON persona.dni = alumnos.dni";
			
			ResultSet mostrar = sentencia.executeQuery(sql);
			
			System.out.println("Pre while");
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Personas </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>-------------------------------</p>");
			while (mostrar.next()) {	
				String dni = mostrar.getString("dni");
				String nombre = mostrar.getString("nombre");
				String apellido = mostrar.getString("apellido");
				//Integer anyo = mostrar.getInt("anyo_inscripcion");
				//String ciclo = mostrar.getString("ciclo");
				System.out.println("Dni: "+dni);
				System.out.println("Nombre: "+nombre);
				Alumno encontrados = new Alumno(dni,nombre,apellido,0,"");
				//Alumno encontrados = new Alumno(dni,nombre,apellido,anyo,ciclo);

				out.println("<p> <b>DNI:</b> " + encontrados.getDni() + " | ");
				out.print(" <b>Nombre:</b> " + encontrados.getNombre() + " | ");
				out.print(" <b>Apellido:</b> " + encontrados.getApellido());
				//out.print(" <b>Año:</b> " + encontrados.getAnyoInscripcion() + " | "); 
				//out.print(" <b>Ciclo:</b> " + encontrados.getCiclo() + "</p>");
				out.println("<p>-------------------------------</p>");
			}
			out.println("<a href='index.html'> <button> Volver </button> </a>");
			out.println("</body>");
			out.println("</html>");
			
			System.out.println("Post while");
			
			con.close();

		} catch(Exception e) {
			System.err.println("Error "+ e);
		}
	}
}
