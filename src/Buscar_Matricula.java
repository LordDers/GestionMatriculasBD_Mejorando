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
 * Servlet implementation class Buscar_Matricula
 */
public class Buscar_Matricula extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buscar_Matricula() {
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

		String referencia=request.getParameter("dniMatricula");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + referencia);
			
			sql="SELECT matriculas.anyo_matriculacion, matriculas.precio, alumnos.ciclo, personas.nombre AS 'alumno', personas.apellido, asignaturas.nombre AS 'asignatura', profesores.dni AS 'profesor' "+
					"FROM (matriculas INNER JOIN alumnos ON matriculas.dni_alumno = alumnos.dni) "+
					"INNER JOIN personas ON alumnos.dni = personas.dni "+
					"INNER JOIN asignaturas ON matriculas.id_asignatura = asignaturas.id_asignatura "+
					"INNER JOIN profesores ON asignaturas.dni_profesor = profesores.dni "+
					"WHERE matriculas.dni_alumno = \""+referencia+"\"";
			System.out.println("Sql: " + sql);			
			
			ResultSet buscar = sentencia.executeQuery(sql);
			//int cont = 0;
			
			Profesor profesor = new Profesor("", "", "", "", "");
			Alumno alumno = new Alumno("", "", "", 0, "");
			Asignatura asignatura = new Asignatura("", 0, profesor);
			Matricula matricula = new Matricula("", 0, profesor, 0, 0);
			
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
				out.println("<title> Buscar Matricula </title>");
				out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
			out.println("</head>");
			out.println("<body>");
			out.println("<table align=\"center\" border=5><tr>");
				out.println("<th>Alumno</th>");
				out.println("<th>Asignatura</th>");
				out.println("<th>Precio</th>");
				out.println("<th>Año de matriculación</th>");
				out.println("<th>Profesor</th>");
			out.println("</tr>");

			while (buscar.next()) {
				matricula.setAnyoMatriculacion(buscar.getInt("anyo_matriculacion"));
				matricula.setPrecio(buscar.getInt("precio"));
				alumno.setCiclo(buscar.getString("ciclo"));
				alumno.setNombre(buscar.getString("alumno"));
				alumno.setApellido(buscar.getString("apellido"));
				asignatura.setNombre(buscar.getString("asignatura"));
				profesor.setDni(buscar.getString("profesor"));
				
				System.out.println("Alumno matriculado: " + alumno.getNombre() + " " + alumno.getApellido());
				System.out.println("Asignatura: " + asignatura.getNombre());
				System.out.println("Precio: " + matricula.getPrecio());
				System.out.println("Año de matriculación: " + matricula.getAnyoMatriculacion());
				System.out.println("Profesor: " + profesor.getDni());
				
				out.println("<tr>");
					out.println("<td>" + alumno.getNombre() + " " + alumno.getApellido() + "</td>");
					out.println("<td>" + asignatura.getNombre() + "</td>");
					out.println("<td>" + matricula.getPrecio() + "</td>");		
					out.println("<td>" + matricula.getAnyoMatriculacion() + "</td>");		
					out.println("<td>" + asignatura.getProfesor().getDni() + "</td>");
				out.println("</tr>");
				//cont++;
			}
			out.println("<tr>");
				out.println("<td colspan=6>");
					out.println("<center> <a href='matriculas.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
			out.println("</tr></table>");
			out.println("</body>");
			out.println("</html>");
			
			/*if (cont < 0) {
				response(response, "No se encontró el alumno");
			}*/
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el alumno");
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	// Respuesta simple
	/*private void response(HttpServletResponse response, String msg) throws IOException {
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
	}*/
	
	// Buscar y Añadir
	/*private void responseMatricula(HttpServletResponse response, Matricula matricula, Alumno alumno, Asignatura asignatura, int contador) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Buscar Matricula </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");
		out.println("<table align=\"center\" border=5><tr>");
			out.println("<th>Alumno</th>");
			out.println("<th>Asignatura</th>");
			out.println("<th>Precio</th>");
			out.println("<th>Año de matriculación</th>");
			out.println("<th>Profesor</th>");
		out.println("</tr>");
		for (int i=0; i<contador; i++) {
			out.println("<tr>");
				out.println("<td>" + alumno.getNombre() + " " + alumno.getApellido() + "</td>");
				out.println("<td>" + asignatura.getNombre() + "</td>");
				out.println("<td>" + matricula.getPrecio() + "</td>");		
				out.println("<td>" + matricula.getAnyoMatriculacion() + "</td>");		
				out.println("<td>" + asignatura.getProfesor().getDni() + "</td>");
			out.println("</tr>");
		}
		out.println("<tr>");
			out.println("<td colspan=6>");
				out.println("<center> <a href='matriculas.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
		out.println("</tr></table>");
		out.println("</body>");
		out.println("</html>");
	}*/
}
