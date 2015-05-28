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
 * Servlet implementation class Buscar_Alumno
 */
public class Buscar_Alumno extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buscar_Alumno() {
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

		String referencia=request.getParameter("alumno");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + referencia);
			
			sql="SELECT personas.dni, personas.nombre, personas.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM personas INNER JOIN alumnos ON personas.dni = alumnos.dni WHERE personas.dni=\""+referencia+"\"";
			System.out.println("Sql: "+sql);			
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			
			Alumno alumno = new Alumno("", "", "", 0, "");

			while (buscar.next()) {
				alumno.setDni(buscar.getString("dni"));
				alumno.setNombre(buscar.getString("nombre"));
				alumno.setApellido(buscar.getString("apellido"));
				alumno.setAnyoInscripcion(buscar.getInt("anyo_inscripcion"));
				alumno.setCiclo(buscar.getString("ciclo"));
				
				System.out.println("Dni: " + alumno.getDni());
				System.out.println("Nombre: " + alumno.getNombre());
				System.out.println("Apellido: " + alumno.getApellido());
				System.out.println("Departamento: " + alumno.getCiclo());		
				cont++;
			}
			
			if (cont > 0) {
				response(response, alumno);
			} else {
				response(response, "No se encontró el alumno");
			}
			con.close();
		
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro el alumno");
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
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	// Buscar y Añadir
	private void response(HttpServletResponse response, Alumno encontrado) throws IOException {
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
				out.println("<center> <a href='alumnos.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
		out.println("</tr></table>");
		out.println("</body>");
		out.println("</html>");
	}

}
