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
 * Servlet implementation class Anyadir_Matricula
 */
public class Anyadir_Matricula extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Anyadir_Matricula() {
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
		
		String dniAlumno = request.getParameter("dniAlumno");
		String asignatura = request.getParameter("asignatura");
		
		Profesor profesor = new Profesor("", "", "", "", "");
		Matricula matricula = new Matricula(asignatura, 0, profesor, 0, 0);
		matricula.setAnyoMatriculacion(Integer.parseInt(request.getParameter("anyo_matricula")));
		matricula.setPrecio(Integer.parseInt(request.getParameter("precio")));
		
		System.out.println("DNI Alumno: " + dniAlumno);
		System.out.println("Asignatura: " + asignatura);
		System.out.println("Año matricula: " + matricula.getAnyoMatriculacion());
		System.out.println("Precio: " + matricula.getPrecio());
		
		Connection con = null;	
		Statement sentencia = null;
		Statement sentenciaAsignatura = null;
		Statement sentenciaInsert = null;
		
		try {
			
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sqlSelect = "SELECT alumnos.dni, personas.nombre, personas.apellido, alumnos.anyo_inscripcion, alumnos.ciclo FROM personas INNER JOIN alumnos ON personas.dni = alumnos.dni WHERE personas.dni=\""+dniAlumno+"\"";
			ResultSet buscar = sentencia.executeQuery(sqlSelect);
			
			int contAlumno = 0;

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
				System.out.println("Año de inscripción: " + alumno.getAnyoInscripcion());
				System.out.println("Ciclo: " + alumno.getCiclo());				
				contAlumno++;
			}
			
			if (contAlumno > 0) {
				System.out.println("Alumno encontrado");
				
				sentenciaAsignatura = con.createStatement();
				
				String sqlSelectAsignatura = "SELECT asignaturas.id_asignatura, asignaturas.nombre AS 'asignatura', asignaturas.creditos, profesores.dni, personas.nombre AS 'profesor', personas.apellido "+
						"FROM (asignaturas INNER JOIN profesores ON asignaturas.dni_profesor = profesores.dni) "+
						"INNER JOIN personas ON profesores.dni = personas.dni "+
						"WHERE asignaturas.nombre=\""+asignatura+"\"";
				System.out.println(sqlSelectAsignatura);
				ResultSet buscarAsignatura = sentenciaAsignatura.executeQuery(sqlSelectAsignatura);
				
				int contAsignatura = 0;
				
				Asignatura asignatura_Ob = new Asignatura("", 0, profesor);
				int id_asig = -1;
				
				while (buscarAsignatura.next()) {
					profesor.setDni(buscarAsignatura.getString("dni"));
					profesor.setNombre(buscarAsignatura.getString("profesor"));
					profesor.setApellido(buscarAsignatura.getString("apellido"));
					
					asignatura_Ob = new Asignatura("", 0, profesor);
					asignatura_Ob.setNombre(buscarAsignatura.getString("asignatura"));
					asignatura_Ob.setCreditos(buscarAsignatura.getInt("creditos"));
					id_asig = buscarAsignatura.getInt("id_asignatura");
					
					System.out.println("Asignatura: " + asignatura_Ob.getNombre());
					System.out.println("Id Asignatura: " + id_asig);
					System.out.println("Dni Profesor: " + profesor.getDni());
					System.out.println("Nombre profesor: " + asignatura_Ob.getProfesor().getNombre());
					System.out.println("Apellido profesor: " + profesor.getApellido());
					System.out.println("Créditos: " + asignatura_Ob.getCreditos());			
					contAsignatura++;
				}
				
				if (contAsignatura > 0) {
					System.out.println("Asignatura correcta");
					
					sentenciaInsert = con.createStatement();
					
					String insertMatricula = "INSERT INTO matriculas VALUES ("+
							"\""+alumno.getDni()+"\", "+
							id_asig+", "+
							matricula.getAnyoMatriculacion()+", "+
							matricula.getPrecio()+")";
					
					System.out.println(insertMatricula);
					
					sentenciaInsert.executeUpdate(insertMatricula);
					
					System.out.println("insertado");
					
					matricula = new Matricula(asignatura_Ob.getNombre(), asignatura_Ob.getCreditos(), profesor, matricula.getAnyoMatriculacion(), matricula.getPrecio());
					responseMatricula(response, matricula, alumno, asignatura_Ob);
				} else {
					System.out.println("La asignatura introducida no está disponible");
					response(response, "La asignatura introducida no está disponible", "relleno");
				}
			} else {
				System.out.println("El DNI introducido no es alumno");
				response(response, "El DNI introducido no es alumno", "relleno");
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
		out.println("<a href='alumnos.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
	
	//Muestra alumno creado
	private void responseMatricula(HttpServletResponse response, Matricula matricula, Alumno alumno, Asignatura asignatura) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<form name=\"buscar_matricula\" method=\"post\" onsubmit=\"return validacion_busqueda_alumno()\" action=\"Buscar_Matricula\">");
				out.println("<label> Matricula creada: </label>");
				out.println("<input name='dniMatricula' type='text' value='" + alumno.getDni() + "' hidden='true'/> <br>");
				out.println("Alumno: <input name=\"alumno\" type=\"text\" value=\""+alumno.getNombre()+"\" placeholder=\""+alumno.getNombre()+"\" disabled/>  <br>");
				out.println("Asignatura: <input name=\"asignatura\" type=\"text\" value=\""+asignatura.getNombre()+"\" placeholder=\""+asignatura.getNombre()+"\" disabled/>  <br>");
				out.println("Profesor: <input name=\"profesor\" type=\"text\" value=\""+asignatura.getProfesor().getNombre()+"\" placeholder=\""+asignatura.getProfesor().getNombre()+"\" disabled/>  <br>");
				out.println("Año de la matricula: <input name=\"anyo_matricula\" type=\"text\" value=\""+matricula.getAnyoMatriculacion()+"\" placeholder=\""+matricula.getAnyoMatriculacion()+"\" disabled/>  <br>");
				out.println("Precio: <input name=\"precio\" type=\"text\" value=\""+matricula.getPrecio()+"\" placeholder=\""+matricula.getPrecio()+"\" disabled/>  <br>");
				out.println("<input type=\"submit\" id=\"submit\" value=\"Mostrar\">");
				out.println("</form>");
		out.println("<a href='matriculas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
