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

import com.zubiri.matriculas.Asignatura;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Mostrar_Asignaturas
 */
public class Mostrar_Asignaturas extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mostrar_Asignaturas() {
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
			sql = "SELECT profesores.dni, persona.nombre AS 'profesor', persona.apellido, profesores.titulacion, profesores.departamento, asignaturas.nombre AS 'asignatura', asignaturas.creditos "+
			"FROM (persona INNER JOIN profesores ON persona.dni = profesores.dni) "+
			"INNER JOIN asignaturas ON profesores.dni = asignaturas.dni_profesor";
			System.out.println(sql);
			
			ResultSet mostrar = sentencia.executeQuery(sql);
			
			System.out.println("Pre while");
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Asignaturas </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>-------------------------------</p>");
			
			Profesor profesor = new Profesor("", "", "", "", "");
			
			while (mostrar.next()) {
				profesor.setDni(mostrar.getString("dni"));
				profesor.setNombre(mostrar.getString("profesor"));
				profesor.setApellido(mostrar.getString("apellido"));
				profesor.setTitulacion(mostrar.getString("titulacion"));
				profesor.setDepartamento(mostrar.getString("departamento"));
				
				Asignatura asignatura = new Asignatura("", 0, profesor);
				asignatura.setNombre(mostrar.getString("asignatura"));
				asignatura.setCreditos(mostrar.getInt("creditos"));
				
				System.out.println("Asignatura: " + asignatura.getNombre());
				System.out.println("Dni Profesor: " + profesor.getDni());
				System.out.println("Nombre: " + profesor.getNombre());
				System.out.println("Apellido: " + profesor.getApellido());
				System.out.println("Créditos: " + asignatura.getCreditos());

				out.println("<p> <b>Asignatura</b> " + asignatura.getNombre() + " | ");
				out.println("<p> DNI Profesor: " + asignatura.getProfesor().getDni() + " | ");
				out.print(" <b>Nombre:</b> " + profesor.getNombre() + " | ");
				out.print(" <b>Apellido:</b> " + profesor.getApellido() + " | ");
				out.print(" <b>Créditos:</b> " + asignatura.getCreditos());
				
				out.println("<p>-------------------------------</p>");
			}
				out.println("<a href='asignaturas.html'> <button> Volver </button> </a>");
			out.println("</body>");
			out.println("</html>");
			System.out.println("Post while");

			con.close();    
			//response(response, dni, nombre, apellido, departamento);

		} catch(Exception e) {
			System.out.println("En el catch 2");
			System.err.println("Error "+ e);
		}
	}
}
