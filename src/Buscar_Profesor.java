

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

import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Buscar_Profesor
 */
public class Buscar_Profesor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buscar_Profesor() {
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

		String referencia=request.getParameter("profesor");
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;		    
			System.out.println("Referencia: " + referencia);
			
			sql="SELECT personas.dni, personas.nombre, personas.apellido, profesores.titulacion, profesores.departamento FROM personas INNER JOIN profesores ON personas.dni = profesores.dni WHERE personas.dni=\""+referencia+"\"";
			System.out.println("Sql: "+sql);			
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			
			Profesor profesor = new Profesor("", "", "", "", "");

			while (buscar.next()) {
				profesor.setDni(buscar.getString("dni"));
				profesor.setNombre(buscar.getString("nombre"));
				profesor.setApellido(buscar.getString("apellido"));
				profesor.setTitulacion(buscar.getString("titulacion"));
				profesor.setDepartamento(buscar.getString("departamento"));
				
				System.out.println("Dni: " + profesor.getDni());
				System.out.println("Nombre: " + profesor.getNombre());
				System.out.println("Apellido: " + profesor.getApellido());
				System.out.println("Departamento: " + profesor.getDepartamento());			
				cont++;
			}
			
			if (cont > 0) {
				response(response, profesor);
			} else {
				response(response, "No se encontró el profesor");
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
		out.println("<a href='profesores.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	// Buscar y Añadir
	private void response(HttpServletResponse response, Profesor encontrado) throws IOException {
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
				out.println("<center> <a href='profesores.html'> <button> Volver </button> </a> </center>");
			out.println("</td>");
		out.println("</tr></table>");
		out.println("</body>");
		out.println("</html>");
	}
}
