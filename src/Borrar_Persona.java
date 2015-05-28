

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
 * Servlet implementation class Borrar_Persona
 */
public class Borrar_Persona extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String USUARIO="root";
	private static final String CONTRA="zubiri";
	private static final String URL_BD="jdbc:mysql://localhost/matriculasBD2";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Borrar_Persona() {
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
		System.out.println("Borrando");
		
		String dni = request.getParameter("dniPersona");
		
		//String sentenciado = request.getParameter("dniAlumno");
		Boolean confirmacion = Boolean.parseBoolean(request.getParameter("confirmacion"));

		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
			
			sentencia = con.createStatement();
			
			String sql;
			System.out.println("Referencia: " + dni);
			
			sql="SELECT * FROM personas WHERE dni=\""+dni+"\"";
			
			ResultSet buscar = sentencia.executeQuery(sql);
			int cont = 0;
			while (buscar.next()) {
				cont++;
			}
			
			if (cont > 0) {
				System.out.println("Contador: " + cont);
				if (confirmacion != true) {
					confirmacion = false;
					response(response, "¿Seguro que quieres borrar la persona?", dni);
				} else {
					Statement sentenciaAlumno = null;
					Statement sentenciaProfesor = null;
					String sqlDelete;
					try {					
						// Register JDBC driver
						Class.forName("com.mysql.jdbc.Driver");
						// Open a connection
						con = DriverManager.getConnection(URL_BD,USUARIO,CONTRA);
						
						// Borrar alumno
						sentenciaAlumno = con.createStatement();

						String sqlSelectAlumno="SELECT personas.dni, personas.nombre, personas.apellido, alumnos.ciclo, alumnos.anyo_inscripcion FROM personas INNER JOIN alumnos ON personas.dni = alumnos.dni WHERE personas.dni=\""+dni+"\"";
						ResultSet mostrarAlumno = sentenciaAlumno.executeQuery(sqlSelectAlumno);
						
						String nombre = null;
						String apellido = null;
						Integer anyo = 0;
						String ciclo = null;
						while (mostrarAlumno.next()) {
						//for (int i=0; mostrarAlumno.next(); i++) {
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
							
							System.out.println("DELETE FROM personas where dni=\""+dni+"\"");
							System.out.println("DELETE FROM alumnos where dni=\""+dni+"\"");
							
							sqlDelete="DELETE FROM personas where dni=\""+dni+"\"";
							String sqlAlumno="DELETE FROM alumnos where dni=\""+dni+"\"";
							
							int borrarAlumno = sentencia.executeUpdate(sqlAlumno);
							int borrarPersona = sentencia.executeUpdate(sqlDelete);
							
							System.out.println("Valor borrar: " + borrarPersona);
							if (borrarPersona == 1) {
								if (borrarAlumno == 1) {
									response(response, "Se ha borrado el alumno " + alumnoEncontrado.getDni());
								}
							} else {
								response(response, "No se ha borrado el alumno, compruebe el DNI: " + alumnoEncontrado.getDni() + ".");
							}
						}
						
						// Borrar profesor
						sentenciaProfesor = con.createStatement();
						
						String sqlSelectProfesor="SELECT personas.dni, personas.nombre, personas.apellido, profesores.titulacion, profesores.departamento FROM personas INNER JOIN profesores ON personas.dni = profesores.dni WHERE personas.dni=\""+dni+"\"";
						ResultSet mostrarProfesor = sentenciaProfesor.executeQuery(sqlSelectProfesor);
						
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
							
							System.out.println("DELETE FROM personas where dni=\""+profesorEncontrado.getDni()+"\"");
							System.out.println("DELETE FROM profesores where dni=\""+profesorEncontrado.getDni()+"\"");
							
							sqlDelete="DELETE FROM personas where dni=\""+dni+"\"";
							String sqlProfesor="DELETE FROM profesores where dni=\""+profesorEncontrado.getDni()+"\"";
							
							int borrarProfesor = sentencia.executeUpdate(sqlProfesor);
							int borrarPersona = sentencia.executeUpdate(sqlDelete);
							
							System.out.println("Valor borrar: " + borrarPersona);
							if (borrarPersona == 1) {
								if (borrarProfesor == 1) {
									response(response, "Se ha borrado el profesor " + profesorEncontrado.getDni());
								}
							} else {
								response(response, "No se ha borrado el profesor, compruebe el DNI: " + profesorEncontrado.getDni() + ".");
							}
						}
						con.close();			    	
					} catch(ArrayIndexOutOfBoundsException e) {
						//response(response, "no se encontro la persona");
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				response(response, "No se encontró la persona");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//response(response, "no se encontro la persona");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void response(HttpServletResponse response,String msg) throws IOException {
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
	
	private void response(HttpServletResponse response,String msg ,String dni) throws IOException {
		response.setContentType( "text/html; charset=iso-8859-1" );
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
			out.println("<title> Borrar persona </title>");
			out.println("<link rel='stylesheet' type='text/css' href='stylebd.css'>");
		out.println("</head>");
		out.println("<body align='center'>");
		out.println("<p>" + msg + "</p>");
		out.println("<p>DNI: " + dni + "</p>");
		out.println("<form name=\"borrar_persona\" method=\"post\" action=\"Borrar_Persona\" style='margin-right: auto;'>");
			out.println("<input name='gestion' hidden='true' type='text'  value='borrar_persona'/>");
			out.println("<input name=\"dniPersona\" hidden=\"true\" type=\"text\"  value=" + dni + "></input>");
			out.println("<input name=\"confirmacion\" hidden=\"true\" type=\"text\"  value='true'></input>");
			out.println("<p> <input type='submit' id='submit' value='Borrar'> </p>");
		out.println("</form>");
		out.println("<a href='personas.html'> <button> Volver </button> </a>");
		out.println("</body>");
		out.println("</html>");
	}
}
