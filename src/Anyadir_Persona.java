import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Alumno;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Anyadir_Persona
 */
public class Anyadir_Persona extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";

    /**
     * Default constructor. 
     */
    public Anyadir_Persona() {
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
		
		Connection con = null;	
		Statement sentencia = null;
		
		try {
			System.out.println("Empieza añadiendo");
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String dni = request.getParameter("dni");
			String nombre = request.getParameter("nombre");
			String apellido = request.getParameter("apellido");
			String persona = request.getParameter("persona");
			/*int anyo = Integer.parseInt(request.getParameter("anyo_inscripcion"));
			String ciclo = request.getParameter("ciclo");*/
			System.out.println("DNI: " + dni);
			System.out.println("Nombre: " + nombre);
			System.out.println("Apellido: " + apellido);
			
			String sql;
			
			System.out.println("INSERT INTO persona VALUES (\""+
					dni+"\",\""+
					nombre+"\",\""+
					apellido+"\")");
			
			sql="INSERT INTO persona VALUES (\""+
					dni+"\",\""+
					nombre+"\",\""+
					apellido+"\")";
			
			sentencia.executeUpdate(sql);
			
			String alumno = "alumno";
			String profesor = "profesor";
			
			if (persona.equals(alumno)) {
				int anyo = Integer.parseInt(request.getParameter("anyo_inscripcion"));
				String ciclo = request.getParameter("ciclo");
				System.out.println("Año de inscripción: " + anyo);
				System.out.println("Ciclo: " + ciclo);
				
				Alumno nuevoAlumno = new Alumno(dni,nombre,apellido,anyo,ciclo);
				
				System.out.println("INSERT INTO alumnos VALUES (\""+
						nuevoAlumno.getDni()+"\","+
						nuevoAlumno.getAnyoInscripcion()+",\""+
						nuevoAlumno.getCiclo()+"\")");
				
				sql="INSERT INTO alumnos VALUES (\""+
						nuevoAlumno.getDni()+"\","+
						nuevoAlumno.getAnyoInscripcion()+",\""+
						nuevoAlumno.getCiclo()+"\")";
				
				sentencia.executeUpdate(sql);
				
			} else if (persona.equals(profesor)) {
				String titulacion = request.getParameter("titulacion");
				String departamento = request.getParameter("departamento");
				System.out.println("Titulación: " + titulacion);
				System.out.println("Departamento: " + departamento);
				
				Profesor nuevoProfesor = new Profesor(dni,nombre,apellido, titulacion, departamento);
				
				System.out.println("INSERT INTO profesores VALUES (\""+
						nuevoProfesor.getDni()+"\",\""+
						nuevoProfesor.getTitulacion()+"\",\""+
						nuevoProfesor.getDepartamento()+"\")");
				
				sql="INSERT INTO profesores VALUES (\""+
						nuevoProfesor.getDni()+"\",\""+
						nuevoProfesor.getTitulacion()+"\",\""+
						nuevoProfesor.getDepartamento()+"\")";
				
				sentencia.executeUpdate(sql);
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el vehiculo");
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	// Respuesta simple
	private void response(HttpServletResponse response, String msg, String relleno) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Respuesta </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");				
		out.println("<p>" + msg + "</p>");
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	//muestra de alumno matriculado
	private void response(HttpServletResponse response, String dni) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<form name=\"buscar_alumno\" method=\"post\" onsubmit=\"return validacion_busqueda_alumno()\" action=\"BuscarAlumno\">");
				out.println("<label> Alumno matriculado: </label>");
				out.println("<input name=\"alumno\" type=\"text\" value=\""+dni+"\" placeholder=\""+dni+"\"/>");
				out.println("<input type=\"submit\" id=\"submit\" value=\"mostrar\">");
				out.println("</form>");
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
