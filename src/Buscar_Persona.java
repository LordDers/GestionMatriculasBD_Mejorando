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
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Buscar_Persona
 */
public class Buscar_Persona extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buscar_Persona() {
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
		
		Connection con = null;	
		Statement sentencia = null;
		
		System.out.println("Empieza buscando");

		String referencia=request.getParameter("persona");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + referencia);
			
			sql="SELECT * FROM personas WHERE dni=\""+referencia+"\"";
			System.out.println("Sql: " + sql);			
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			String dni = null;
			String nombre = null;
			String apellido = null;
			
			while (buscar.next()) {
				dni = buscar.getString("dni");
				nombre = buscar.getString("nombre");
				apellido = buscar.getString("apellido");
				cont++;
			}
			
			if (cont > 0) {
				//response(response, encontrado);
				
				sql="SELECT personas.dni, personas.nombre, personas.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM personas INNER JOIN alumnos ON personas.dni = alumnos.dni WHERE personas.dni=\""+referencia+"\"";
				ResultSet mostrarAlumno = sentencia.executeQuery(sql);
				
				Integer anyo = 0;
				String ciclo = null;
				while (mostrarAlumno.next()) {	
					dni = mostrarAlumno.getString("dni");
					nombre = mostrarAlumno.getString("nombre");
					apellido = mostrarAlumno.getString("apellido");
					anyo = mostrarAlumno.getInt("anyo_inscripcion");
					ciclo = mostrarAlumno.getString("ciclo");
					
					Alumno alumnoEncontrado = new Alumno(dni,nombre,apellido,anyo,ciclo);
					
					System.out.println("Dni: " + alumnoEncontrado.getDni());
					System.out.println("Nombre: " + alumnoEncontrado.getNombre());
					System.out.println("Apellido: " + alumnoEncontrado.getApellido());
					System.out.println("Ciclo: " + alumnoEncontrado.getCiclo());
					responseAlumno(response, alumnoEncontrado);
					//response(response,"No se encontró el alumno");
				}
				
				sql="SELECT personas.dni, personas.nombre, personas.apellido, profesores.titulacion, profesores.departamento FROM personas INNER JOIN profesores ON personas.dni = profesores.dni WHERE personas.dni=\""+referencia+"\"";
				ResultSet mostrarProfesor = sentencia.executeQuery(sql);
				
				String titulacion = null;
				String departamento = null;
				while (mostrarProfesor.next()) {	
					dni = mostrarProfesor.getString("dni");
					nombre = mostrarProfesor.getString("nombre");
					apellido = mostrarProfesor.getString("apellido");
					titulacion = mostrarProfesor.getString("titulacion");
					departamento = mostrarProfesor.getString("departamento");
					
					Profesor profesorEncontrado = new Profesor(dni,nombre,apellido, titulacion, departamento);
					
					System.out.println("Dni: " + profesorEncontrado.getDni());
					System.out.println("Nombre: " + profesorEncontrado.getNombre());
					System.out.println("Apellido: " + profesorEncontrado.getApellido());
					System.out.println("Departamento: " + profesorEncontrado.getDepartamento());
					
					responseProfesor(response, profesorEncontrado);
				}
			} else {
				response(response, "No se encontró la persona");
				System.out.println("No se encontró la persona");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro la persona");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Respuesta simple
	private void response(HttpServletResponse response, String msg) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Respuesta </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");				
		out.println("<p>" + msg + "</p>");
		out.println("<a href='personas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
		
	// Alumno
	private void responseAlumno(HttpServletResponse response, Alumno encontrado) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Buscar Alumno </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");
		out.println("<table align=\"center\" border=5><tr>");
			out.println("<th>DNI</th>");
			out.println("<th>Nombre</th>");
			out.println("<th>Apellido</th>");
			out.println("<th>Año de Inscripción</th>");
			out.println("<th>Ciclo</th>");
		out.println("</tr><tr>");
			out.println("<td>" + encontrado.getDni() + "</td>");
			out.println("<td>" + encontrado.getNombre() + "</td>");
			out.println("<td>" + encontrado.getApellido() + "</td>");		
			out.println("<td>" + encontrado.getAnyoInscripcion() + "</td>");		
			out.println("<td>" + encontrado.getCiclo() + "</td>");
		out.println("</tr><tr>");
			out.println("<td colspan=6>");
				out.println("<center> <a href='personas.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
		out.println("</tr></table>");
		out.println("</body>");
		out.println("</html>");
	}
	
	// Profesor
	private void responseProfesor(HttpServletResponse response, Profesor encontrado) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Buscar Profesor </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");
		out.println("<table align=\"center\" border=5><tr>");
			out.println("<th>DNI</th>");
			out.println("<th>Nombre</th>");
			out.println("<th>Apellido</th>");
			out.println("<th>Titulación</th>");
			out.println("<th>Departamento</th>");
		out.println("</tr><tr>");
			out.println("<td>" + encontrado.getDni() + "</td>");
			out.println("<td>" + encontrado.getNombre() + "</td>");
			out.println("<td>" + encontrado.getApellido() + "</td>");		
			out.println("<td>" + encontrado.getTitulacion() + "</td>");		
			out.println("<td>" + encontrado.getDepartamento() + "</td>");
		out.println("</tr><tr>");
			out.println("<td colspan=6>");
				out.println("<center> <a href='personas.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
		out.println("</tr></table>");
		out.println("</body>");
		out.println("</html>");
	}
}
