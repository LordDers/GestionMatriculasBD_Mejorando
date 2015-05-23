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
 * Servlet implementation class Mostrar_Alumnos
 */
public class Mostrar_Alumnos extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Mostrar_Alumnos() {
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
			sql="SELECT persona.dni, persona.nombre, persona.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM persona INNER JOIN alumnos ON persona.dni = alumnos.dni";
			ResultSet mostrar = sentencia.executeQuery(sql);
			
			System.out.println("Pre while");
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Alumnos </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>-------------------------------</p>");
			
			Alumno alumno = new Alumno("", "", "", 0, "");
			
			while (mostrar.next()) {
				alumno.setDni(mostrar.getString("dni"));
				alumno.setNombre(mostrar.getString("nombre"));
				alumno.setApellido(mostrar.getString("apellido"));
				alumno.setAnyoInscripcion(mostrar.getInt("anyo_inscripcion"));
				alumno.setCiclo(mostrar.getString("ciclo"));
				
				System.out.println("Dni: " + alumno.getDni());
				System.out.println("Nombre: " + alumno.getNombre());
				System.out.println("Apellido: " + alumno.getApellido());
				System.out.println("Departamento: " + alumno.getCiclo());	

				out.println("<p> <b>DNI:</b> " + alumno.getDni() + " | ");
				out.print(" <b>Nombre:</b> " + alumno.getNombre() + " | ");
				out.print(" <b>Apellido:</b> " + alumno.getApellido() + " | ");
				out.print(" <b>Departamento:</b> " + alumno.getCiclo());
				
				out.println("<p>-------------------------------</p>");
			}
				out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
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
