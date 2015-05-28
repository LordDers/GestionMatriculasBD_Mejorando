

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
 * Servlet implementation class Buscar_Asignatura
 */
public class Buscar_Asignatura extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buscar_Asignatura() {
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

		String referencia=request.getParameter("asignatura");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + referencia);
			
			sql="SELECT profesores.dni, personas.nombre AS 'profesor', personas.apellido, profesores.titulacion, profesores.departamento, asignaturas.nombre AS 'asignatura', asignaturas.creditos "+
					"FROM (personas INNER JOIN profesores ON personas.dni = profesores.dni) "+
					"INNER JOIN asignaturas ON profesores.dni = asignaturas.dni_profesor "+
					"WHERE asignaturas.nombre=\""+referencia+"\"";
			System.out.println("Sql: "+sql);			
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			
			Profesor profesor = new Profesor("", "", "", "", "");
			Asignatura asignatura = new Asignatura("", 0, profesor);

			while (buscar.next()) {
				profesor.setDni(buscar.getString("dni"));
				profesor.setNombre(buscar.getString("profesor"));
				profesor.setApellido(buscar.getString("apellido"));
				profesor.setTitulacion(buscar.getString("titulacion"));
				profesor.setDepartamento(buscar.getString("departamento"));
				
				asignatura = new Asignatura("", 0, profesor);
				asignatura.setNombre(buscar.getString("asignatura"));
				asignatura.setCreditos(buscar.getInt("creditos"));
				
				System.out.println("Asignatura: " + asignatura.getNombre());
				System.out.println("Dni Profesor: " + profesor.getDni());
				System.out.println("Nombre: " + profesor.getNombre());
				System.out.println("Apellido: " + profesor.getApellido());
				System.out.println("Créditos: " + asignatura.getCreditos());			
				cont++;
			}
			
			if (cont > 0) {
				response(response, asignatura);
			} else {
				response(response, "No se encontró la asignatura");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el profesor");
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
		out.println("<a href='asignaturas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	// Buscar y Añadir
	private void response(HttpServletResponse response, Asignatura encontrado) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Buscar Asignatura </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body>");
		out.println("<table align=\"center\" border=5><tr>");
			out.println("<th>Asignatura</th>");
			out.println("<th>Nombre Profesor</th>");
			out.println("<th>Apellido Profesor</th>");
			out.println("<th>Créditos</th>");
		out.println("</tr><tr>");
			out.println("<td>" + encontrado.getNombre() + "</td>");
			out.println("<td>" + encontrado.getProfesor().getNombre() + "</td>");
			out.println("<td>" + encontrado.getProfesor().getApellido() + "</td>");		
			out.println("<td>" + encontrado.getCreditos() + "</td>");
		out.println("</tr><tr>");
			out.println("<td colspan=6>");
				out.println("<center> <a href='asignaturas.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
		out.println("</tr></table>");
		out.println("</body>");
		out.println("</html>");
	}
}
