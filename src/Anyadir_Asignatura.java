import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zubiri.matriculas.Asignatura;
import com.zubiri.matriculas.Profesor;

/**
 * Servlet implementation class Anyadir_Asignatura
 */
public class Anyadir_Asignatura extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Anyadir_Asignatura() {
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
		
		System.out.println("Empieza añadiendo");
		
		String nombre = request.getParameter("nombre");
		int creditos = Integer.parseInt(request.getParameter("creditos"));
		String dni = request.getParameter("dniProfesorAsignatura");
		String id = request.getParameter("id");
		
		System.out.println("Nombre: " + nombre);
		System.out.println("Créditos: " + creditos);
		System.out.println("DNI: " + dni);
		
		Connection con = null;	
		Statement sentencia = null;
		Statement sentenciaSelect = null;
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			sentenciaSelect = con.createStatement();
			
			String sqlSelect = "SELECT profesores.dni, persona.nombre, persona.apellido, profesores.titulacion, profesores.departamento FROM persona INNER JOIN profesores ON persona.dni = profesores.dni WHERE persona.dni=\""+dni+"\"";
			ResultSet buscar = sentenciaSelect.executeQuery(sqlSelect);
			
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
				String sql;
				System.out.println("INSERT INTO asignaturas VALUES ("+
						id+",\""+
						nombre+"\","+
						creditos+",\""+ 
						dni+"\")");
				
				sql = "INSERT INTO asignaturas VALUES ("+
						id+",\""+
						nombre+"\","+
						creditos+",\""+ 
						dni+"\")";
				
				sentencia.executeUpdate(sql);

				Asignatura asignatura = new Asignatura(nombre, creditos, profesor);

				response(response, asignatura);
			} else {
				System.out.println("El DNI introducido no es profesor");
				response(response, "El DNI introducido no es profesor", "relleno");
			}
			con.close();    
			
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
		out.println("<a href='profesores.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	// Muestra asignatura creada
	private void response(HttpServletResponse response, Asignatura asignatura) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<form name=\"buscar_asignatura\" method=\"post\" onsubmit=\"return validacion_busqueda_alumno()\" action=\"Buscar_Asignatura\">");
				out.println("<label> Asignatura creada: </label>");
				out.println("<input name=\"nombre\" type=\"text\" value=\""+asignatura.getNombre()+"\" placeholder=\""+asignatura.getNombre()+"\"/> <br>");
				out.println("Profesor: <input name=\"profe\" type=\"text\" value=\""+asignatura.getProfesor().getNombre()+"\" placeholder=\""+asignatura.getProfesor().getNombre()+"\"/> <br>");
				out.println("<input type=\"submit\" id=\"submit\" value=\"Mostrar\">");
				out.println("</form>");
		out.println("<a href='asignaturas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
