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
import com.zubiri.matriculas.Asignatura;
import com.zubiri.matriculas.Matricula;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Mostrar_Matriculas
 */
public class Mostrar_Matriculas extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mostrar_Matriculas() {
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

		System.out.println("Empieza mostrando");

		try {
			System.out.println("En el try mostrar");
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);

			sentencia = con.createStatement();

			String sql;			
			sql = "SELECT matriculas.anyo_matriculacion, matriculas.precio, alumnos.ciclo, persona.nombre AS 'alumno', persona.apellido, asignaturas.nombre AS 'asignatura', profesores.dni AS 'profesor' "+
				"FROM (matriculas INNER JOIN alumnos ON matriculas.dni_alumno = alumnos.dni) "+
				"INNER JOIN persona ON alumnos.dni = persona.dni "+
				"INNER JOIN asignaturas ON matriculas.id_asignatura = asignaturas.id_asignatura "+
				"INNER JOIN profesores ON asignaturas.dni_profesor = profesores.dni";
			System.out.println(sql);
			
			ResultSet mostrar = sentencia.executeQuery(sql);
			
			System.out.println("Pre while");
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Matriculas </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>-------------------------------</p>");
			
			Profesor profesor = new Profesor("", "", "", "", "");
			Alumno alumno = new Alumno("", "", "", 0, "");
			Asignatura asignatura = new Asignatura("", 0, profesor);
			Matricula matricula = new Matricula("", 0, profesor, 0, 0);
			
			while (mostrar.next()) {
				matricula.setAnyoMatriculacion(mostrar.getInt("anyo_matriculacion"));
				matricula.setPrecio(mostrar.getInt("precio"));
				alumno.setCiclo(mostrar.getString("ciclo"));
				alumno.setNombre(mostrar.getString("alumno"));
				alumno.setApellido(mostrar.getString("apellido"));
				asignatura.setNombre(mostrar.getString("asignatura"));
				profesor.setDni(mostrar.getString("profesor"));
				
				System.out.println("Alumno matriculado: " + alumno.getNombre() + " " + alumno.getApellido());
				System.out.println("Asignatura: " + asignatura.getNombre());
				System.out.println("Precio: " + matricula.getPrecio());
				System.out.println("Año de matriculación: " + matricula.getAnyoMatriculacion());
				System.out.println("Profesor: " + profesor.getDni());

				out.println("<p> <b>Alumno matriculado:</b> " + alumno.getNombre() + " " + alumno.getApellido() + " | ");
				out.println("<p> Asignatura: " + asignatura.getNombre() + " | ");
				out.print(" Precio: " + matricula.getPrecio() + " | ");
				out.print(" Año de matriculación: " + matricula.getAnyoMatriculacion() + " | ");
				out.print(" Profesor: " + profesor.getDni());
				
				out.println("<p>-------------------------------</p>");
			}
				out.println("<a href='matriculas.html'> <button> Volver </button> </a>");
			out.println("</body>");
			out.println("</html>");
			System.out.println("Post while");

			con.close();

		} catch(Exception e) {
			System.out.println("En el catch 2");
			System.err.println("Error "+ e);
		}
	}

}
